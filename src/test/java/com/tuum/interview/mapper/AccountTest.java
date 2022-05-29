package com.tuum.interview.mapper;

import com.tuum.interview.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class AccountTest extends BaseTest {

    @Autowired
    AccountMapper repository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void insertAccountAndReturnID() {
        Account account = Account.builder()
                .country("Country")
                .currency_list(List.of("USD"))
                .customer_id(1)
                .build();

        repository.insert(account);

        assertThat(account.getId(), equalTo(1L));
    }

    @Test
    void canSearchById() {
        Account account = Account.builder()
                .country("Country")
                .currency_list(List.of("USD"))
                .customer_id(1)
                .build();

        Account expected = Account.builder()
                .id(1L)
                .country("Country")
                .currency_list(List.of("USD"))
                .customer_id(1)
                .currency_list(null)
                .balanceList(Collections.emptySet())
                .build();

        repository.insert(account);
        Optional<Account> saved = repository.findById(1L);

        assertThat(saved.get(), sameBeanAs(expected));
    }
}