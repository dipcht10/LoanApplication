package com.example.loanapplication.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanRequest(
        @NotBlank @Size(max = 100) String borrowerName,
        @NotBlank @Size(max = 30) String loanAccountNumber,
        @NotBlank @Size(max = 40) String loanType,
        @NotNull @DecimalMin("0.01") BigDecimal principalAmount,
        @NotNull @DecimalMin("0.00") @Digits(integer = 3, fraction = 2) BigDecimal annualInterestRate,
        @NotNull @Min(1) @Max(600) Integer tenureMonths,
        @NotNull @PastOrPresent LocalDate startDate,
        @NotBlank @Pattern(regexp = "ACTIVE|CLOSED|DEFAULTED|PENDING",
                message = "status must be ACTIVE, CLOSED, DEFAULTED, or PENDING") String status
) {}
