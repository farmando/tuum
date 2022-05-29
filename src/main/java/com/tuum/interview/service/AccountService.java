package com.tuum.interview.service;

import com.tuum.interview.exception.response.ResourceNotFoundException;
import com.tuum.interview.mapper.AccountMapper;
import com.tuum.interview.mapper.BalanceMapper;
import com.tuum.interview.mapper.CurrencyMapper;
import com.tuum.interview.model.Account;
import com.tuum.interview.model.Balance;
import com.tuum.interview.model.RabbitMqEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AccountService {

    private AccountMapper accountMapper;
    private BalanceMapper balanceMapper;
    private CurrencyMapper currencyMapper;
    private final PlatformTransactionManager transactionManager;
    private RabbitMqEventProducer eventProducer;

    public Account createAccount(Account account) {

        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            account.setAccountNumber(UUID.randomUUID().toString());
            accountMapper.insert(account);
            List<String> currencyList = account.getCurrency_list();
            validateCurrencyListOrThrowOutException(currencyList);
            currencyList.forEach(currency -> balanceMapper.insert(toBalance(currency, account)));
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
        transactionManager.commit(transactionStatus);
        eventProducer.sendMessage(RabbitMqEvent.builder().idMessage(UUID.randomUUID()).typeEvent("ACC_CREATION").account(account).build());
        return account;
    }

    public Account findById(Long id) {
        return accountMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Account %d not found", id)));
    }

    private void validateCurrencyListOrThrowOutException(List<String> currencyList) {
        currencyList.forEach(c -> currencyMapper.findById(c)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Invalid currency. The currency %s it is not allowed.", c))));
    }

    private Balance toBalance(String currency, Account account) {

        Balance balance = Balance.builder()
                .account_id(account.getId())
                .currency_id(currency)
                .balance(account.getInitial_balance()).build();

        account.getBalanceList().add(balance);
        return balance;
    }
}
