package com.tuum.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferAmountResponse {
    private Account fromAccount;
    private Account toAccount;
    @JsonProperty("transaction_control")
    private String transactionControl;

}
