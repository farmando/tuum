package com.tuum.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferAmountRequest {
    @NotNull(message = "Field fromAccount can not be NULL")
    private Long fromAccount;
    @NotNull(message = "Field toAccount can not be NULL")
    private Long toAccount;
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than ZERO")
    private BigDecimal amount;
    @JsonProperty("currency_id")
    @Size(min = 3, max = 3, message = "Currency ID must have size of 3")
    private String currencyId;
    @NotEmpty(message = "Field description must have a value")
    private String description;
}
