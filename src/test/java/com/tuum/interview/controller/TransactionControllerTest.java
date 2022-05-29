package com.tuum.interview.controller;

import com.tuum.interview.mapper.AccountMapper;
import com.tuum.interview.mapper.BaseTest;
import com.tuum.interview.mapper.TransactionMapper;
import com.tuum.interview.model.Account;
import com.tuum.interview.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest extends BaseTest {

    @LocalServerPort
    private int port;

    private final String URL = String.format("http://localhost:%d/transaction/", port);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void getTransaction() {
        long accountId = insertAccount(Account.builder().customer_id(1).country("Country").build());
        String trxController = "trx1";
        insertTransaction(accountId, trxController);

        List<Transaction> transaction = this.restTemplate.getForObject("http://localhost:" + port + "/transaction/account/" + accountId, List.class);

        assertNotNull(transaction);
    }

    @Test
    void returnsStatusNotFoundForInexistentAccount() {
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:" + port + "/transaction/account/0", String.class);
        assertEquals(404, result.getStatusCodeValue());
    }

    private void insertTransaction(long accountId, String trxController) {
        Transaction transaction = Transaction.builder()
                .account_id(accountId)
                .trx_controller(trxController)
                .direction("IN")
                .amount(BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP))
                .currency_id("USD")
                .description("test")
                .build();

        transactionMapper.insert(transaction);
    }

    private long insertAccount(Account account) {
        accountMapper.insert(account);
        return account.getId();
    }
}