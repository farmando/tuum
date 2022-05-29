package com.tuum.interview.service;

import com.tuum.interview.mapper.AccountMapper;
import com.tuum.interview.mapper.BalanceMapper;
import com.tuum.interview.mapper.CurrencyMapper;
import com.tuum.interview.model.Account;
import com.tuum.interview.model.Balance;
import com.tuum.interview.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

   @Mock
   private AccountMapper accountMapper;
   @Mock
   private BalanceMapper balanceMapper;
   @Mock
   private CurrencyMapper currencyMapper;
   @Mock
   private PlatformTransactionManager transactionManager;
   @Mock
   TransactionStatus transactionStatus;
   @Mock
   private RabbitMqEventProducer eventProducer;
   @InjectMocks
   AccountService underTest;

    @Test
    void createAccountSetBalanceProperlyForGivenCurrenciesAndSendOutMessage() {
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        Account account = Account.builder().id(1L).customer_id(1).initial_balance(BigDecimal.valueOf(100.00)).country("Estonia").currency_list(List.of("USD", "SEK")).balanceList(new HashSet<>()).build();
        when(currencyMapper.findById(any())).thenReturn(Optional.of(Currency.builder().build()));

        Set<Balance> expecteBalanceList = new HashSet<>();
        expecteBalanceList.add(Balance.builder().account_id(1L).currency_id("SEK").balance(BigDecimal.valueOf(100.00)).build());
        expecteBalanceList.add(Balance.builder().account_id(1L).currency_id("USD").balance(BigDecimal.valueOf(100.00)).build());

        Account saved = underTest.createAccount(account);

        verify(balanceMapper, times(2)).insert(any());
        verify(eventProducer, times(1)).sendMessage(any());
        assertThat(saved.getBalanceList().size(), equalTo(2));

        assertThat(expecteBalanceList, sameBeanAs(saved.balanceList));
    }

    @Test
    void findById() {
        Account account = Account.builder().id(1L).customer_id(1).initial_balance(BigDecimal.valueOf(100.00)).country("Estonia").currency_list(List.of("USD", "SEK")).balanceList(new HashSet<>()).build();
        when(accountMapper.findById(any())).thenReturn(Optional.of(account));

        underTest.findById(account.getId());

        verify(accountMapper, times(1)).findById(account.getId());
    }
}