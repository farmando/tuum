package com.tuum.interview.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    private Long id;
    @NotNull
    private Long account_id;
    private String trx_controller;
    @DecimalMin(value = "0.0", inclusive = false, message = "The amount value must be greater than ZERO")
    private BigDecimal amount;
    @Pattern(regexp = "IN|OUT")
    private String direction;
    @NotNull
    private String currency_id;
    private String description;
}
