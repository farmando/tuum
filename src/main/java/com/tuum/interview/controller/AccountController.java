package com.tuum.interview.controller;

import com.tuum.interview.exception.response.ResourceNotFoundException;
import com.tuum.interview.model.Account;
import com.tuum.interview.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import static org.springframework.util.ObjectUtils.isEmpty;

@AllArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<Account> create(@Valid @RequestBody Account account) {
        Account saved = accountService.createAccount(account);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> findUser(@PathVariable long id) {
        Account account = accountService.findById(id);

        if (isEmpty(account)) {
            throw new ResourceNotFoundException("Account not found");
        }

        return new ResponseEntity<>(account, HttpStatus.FOUND);
    }
}
