package com.tuum.interview.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private Long id;
    @Positive
    private int customer_id;
    @Size(min = 3)
    private String country;
    @JsonProperty(access = WRITE_ONLY)
    @Size(min = 1)
    private List<@NotBlank @Size(max = 3, min = 3) String> currency_list;
    @JsonProperty(access = WRITE_ONLY)
    @NotNull(message = "Initial balance must have a value")
    @DecimalMin(value = "100.00", message = "The initial deposit must be at least 100.00")
    private BigDecimal initial_balance;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("Balance")
    @Builder.Default
    public Set<@Valid Balance> balanceList = new HashSet<>();
}
