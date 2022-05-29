package com.tuum.interview.controller;

import com.tuum.interview.model.Transaction;
import com.tuum.interview.model.TransferAmountRequest;
import com.tuum.interview.model.TransferAmountResponse;
import com.tuum.interview.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private TransactionService transactionService;

    @GetMapping("/account/{id}")
    public ResponseEntity<List<Transaction>> getTransaction(@PathVariable long id) {
        List<Transaction> transaction = transactionService.findByAccountId(id);
        return new ResponseEntity<>(transaction, HttpStatus.FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<TransferAmountResponse> create(@Valid @RequestBody TransferAmountRequest request) {
        TransferAmountResponse response = transactionService.transferAmount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
