package com.tuum.interview.service;

import com.tuum.interview.exception.response.InvalidRequestException;
import com.tuum.interview.exception.response.ResourceNotFoundException;
import com.tuum.interview.mapper.BalanceMapper;
import com.tuum.interview.mapper.CurrencyMapper;
import com.tuum.interview.mapper.TransactionMapper;
import com.tuum.interview.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.util.CollectionUtils.isEmpty;

@AllArgsConstructor
@Service
public class TransactionService {

    private TransactionMapper transactionMapper;
    private BalanceMapper balanceMapper;
    private CurrencyMapper currencyMapper;
    private AccountService accountService;
    private final PlatformTransactionManager transactionManager;
    private RabbitMqEventProducer rabbitMqEventProducer;

    public TransferAmountResponse transferAmount(TransferAmountRequest request) {
        Account fromAccount = accountService.findById(request.getFromAccount());
        Account toAccount = accountService.findById(request.getToAccount());
        getCurrencyById(request.getCurrencyId());

        String transactionControl = executeTransfer(fromAccount, toAccount, request);

        rabbitMqEventProducer.sendMessage(RabbitMqEvent.builder().typeEvent("TRF_CREATION").idMessage(UUID.randomUUID())
                .request(request).build());

        return TransferAmountResponse.builder()
                .fromAccount(accountService.findById(request.getFromAccount()))
                .toAccount(accountService.findById(request.getToAccount()))
                .transactionControl(transactionControl).build();
    }

    public List<Transaction> findByAccountId(long accountId) {
        List<Transaction> transactionList = transactionMapper.findByAccountId(accountId);

        if (isEmpty(transactionList)) {
            throw new ResourceNotFoundException(String.format("There is no transaction for account %d", accountId));
        }

        return transactionList;
    }

    private String executeTransfer(Account fromAccount, Account toAccount, TransferAmountRequest request) {
        String trxControl = UUID.randomUUID().toString();

        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Balance fromAccountBalance = getBalanceByAccountIdAndCurrencyId(fromAccount.getId(), request.getCurrencyId());
            Balance toAccountBalance = getBalanceByAccountIdAndCurrencyId(toAccount.getId(), request.getCurrencyId());

            updateBalance(fromAccountBalance, toAccountBalance, request);

            createTransactionFor(fromAccount, request, trxControl, "OUT");
            createTransactionFor(toAccount, request, trxControl, "IN");

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }

        return trxControl;
    }

    private Currency getCurrencyById(String currencyId) {
        return currencyMapper.findById(currencyId).orElseThrow(() -> new InvalidRequestException(String.format("Invalid currency %s", currencyId)));
    }

    private Balance getBalanceByAccountIdAndCurrencyId(long accountId, String currencyId) {
        return balanceMapper.findByAccountIdAndCurrency(accountId, currencyId).orElseThrow(() ->
                new InvalidRequestException(String.format("Account %d can not operate with %s currency", accountId, currencyId)));
    }

    private void updateBalance(Balance from, Balance to, TransferAmountRequest request) {
        balanceMapper.update(from.getAccount_id(), request.getAmount().multiply(BigDecimal.valueOf(-1)), from.getCurrency_id());
        balanceMapper.update(to.getAccount_id(), request.getAmount(), to.getCurrency_id());
    }

    private void createTransactionFor(Account account, TransferAmountRequest request, String trxControl, String direction) {
        Transaction trx =Transaction.builder()
                .account_id(account.getId())
                .trx_controller(trxControl)
                .amount(request.getAmount())
                .direction(direction)
                .currency_id(request.getCurrencyId())
                .description(request.getDescription())
                .build();
        transactionMapper.insert(trx);
    }
}
