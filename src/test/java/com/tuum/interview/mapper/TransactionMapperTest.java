package com.tuum.interview.mapper;

import com.tuum.interview.model.Account;
import com.tuum.interview.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionMapperTest extends BaseTest {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private AccountMapper accountMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void createTransaction() {
        Transaction transaction = Transaction.builder()
                .account_id(createAccountAndGetAccountId())
                .trx_controller("control")
                .currency_id("USD")
                .amount(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                .direction("IN")
                .description("desc")
                .build();

        transactionMapper.insert(transaction);

        assertNotNull(transaction.getId());
    }

    @Test
    void getListTransaction() {
        Transaction transaction = Transaction.builder()
                .account_id(createAccountAndGetAccountId())
                .trx_controller("control")
                .currency_id("USD")
                .amount(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP))
                .direction("IN")
                .description("desc")
                .build();

        transactionMapper.insert(transaction);

        List<Transaction> transactionList = transactionMapper.findByAccountId(transaction.getAccount_id());

        assertThat(transactionList.size(), equalTo(1));
    }

    private long createAccountAndGetAccountId() {
        Account account = Account.builder()
                .country("Country")
                .currency_list(List.of("USD"))
                .customer_id(1)
                .build();

        accountMapper.insert(account);
        return account.getId();
    }

}