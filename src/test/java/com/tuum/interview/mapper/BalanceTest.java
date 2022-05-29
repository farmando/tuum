package com.tuum.interview.mapper;

import com.tuum.interview.model.Account;
import com.tuum.interview.model.Balance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class BalanceTest extends BaseTest {

    @Autowired
    private BalanceMapper balanceMapper;
    @Autowired
    AccountMapper accountMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void insertBalanceForGivenAccountId() {
        Balance toInsert = Balance.builder()
                .account_id(insertAccountAndReturnsId())
                .currency_id("USD")
                .balance(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                .build();

        balanceMapper.insert(toInsert);
        Optional<Account> savedAccount = accountMapper.findById(toInsert.getAccount_id());
        Optional<Balance> balance = getFirstElementFrom(savedAccount.get().getBalanceList());

        assertThat(savedAccount.get().getBalanceList().size(), equalTo(1));
        Assertions.assertTrue(balance.isPresent());
        assertThat(balance.get().getBalance(), equalTo(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    void doNotInsertWhenCurrencyIdIsLargerThenExpected() {

        DataIntegrityViolationException thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            Balance invalid = Balance.builder()
                    .account_id(insertAccountAndReturnsId())
                    .currency_id("XXXX")
                    .balance(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                    .build();

            balanceMapper.insert(invalid);
        });

        Assertions.assertTrue(Objects.requireNonNull(thrown.getMessage()).contains("value too long for type character varying(3)"));
    }

    @Test
    void findByCustomerIdAndCurrencyId() {
        Balance toInsert = Balance.builder()
                .account_id(insertAccountAndReturnsId())
                .currency_id("USD")
                .balance(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                .build();

        balanceMapper.insert(toInsert);
        Optional<Balance> saved = balanceMapper.findByAccountIdAndCurrency(toInsert.getAccount_id(), "USD");

        Assertions.assertTrue(saved.isPresent());
    }

    @Test
    void update() {
        Balance toInsert = Balance.builder()
                .account_id(insertAccountAndReturnsId())
                .currency_id("USD")
                .balance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        balanceMapper.insert(toInsert);
        balanceMapper.update(toInsert.getAccount_id(), BigDecimal.TEN, toInsert.getCurrency_id());
        Optional<Balance> updated = balanceMapper.findByAccountIdAndCurrency(toInsert.getAccount_id(), toInsert.getCurrency_id());

        Assertions.assertTrue(updated.isPresent());
        assertThat(updated.get().getBalance(), equalTo(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP)));
    }


    private Long insertAccountAndReturnsId() {
        Account account = Account.builder()
                .country("Country")
                .currency_list(List.of("USD"))
                .customer_id(1)
                .build();

        accountMapper.insert(account);
        return account.getId();
    }

    private Optional<Balance> getFirstElementFrom(Set<Balance> balances) {
        return balances.stream().findFirst();
    }
}