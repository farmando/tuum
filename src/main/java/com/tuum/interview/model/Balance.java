package com.tuum.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Balance {
    @JsonProperty(access = WRITE_ONLY)
    private Long id;
    @JsonProperty(access = WRITE_ONLY)
    private Long account_id;
    @Size(min = 3, max = 3)
    @JsonProperty("Currency")
    private String currency_id;
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than ZERO")
    private BigDecimal balance;
}
