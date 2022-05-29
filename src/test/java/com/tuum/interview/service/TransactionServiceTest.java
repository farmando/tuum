package com.tuum.interview.service;

import com.tuum.interview.mapper.BalanceMapper;
import com.tuum.interview.mapper.CurrencyMapper;
import com.tuum.interview.mapper.TransactionMapper;
import com.tuum.interview.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private CurrencyMapper currencyMapper;
    @Mock
    private AccountService accountService;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private RabbitMqEventProducer rabbitMqEventProducer;
    @Mock
    TransactionStatus transactionStatus;
    @InjectMocks
    private TransactionService underTest;

    @Test
    void makeTransfer() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        when(accountService.findById(1l)).thenReturn(Account.builder().id(1L).country("Country").customer_id(1).build());
        when(accountService.findById(2l)).thenReturn(Account.builder().id(2L).country("Country").customer_id(1).build());
        when(currencyMapper.findById(any())).thenReturn(Optional.of(Currency.builder().id("USD").build()));
        when(balanceMapper.findByAccountIdAndCurrency(1L, "USD"))
                .thenReturn(Optional.of(Balance.builder().account_id(1L).currency_id("USD").balance(BigDecimal.TEN).build()));
        when(balanceMapper.findByAccountIdAndCurrency(2L, "USD"))
                .thenReturn(Optional.of(Balance.builder().account_id(2L).currency_id("USD").balance(BigDecimal.TEN).build()));

        TransferAmountResponse response = underTest.transferAmount(TransferAmountRequest.builder()
                .fromAccount(1L)
                .toAccount(2L)
                .amount(BigDecimal.TEN)
                .currencyId("USD")
                .build());

        verify(rabbitMqEventProducer, times(1)).sendMessage(any());
        assertNotNull(response.getTransactionControl());
    }
}