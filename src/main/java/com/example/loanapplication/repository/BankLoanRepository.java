package com.example.loanapplication.repository;

import com.example.loanapplication.entity.BankLoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankLoanRepository extends JpaRepository<BankLoan, Long> {
    Optional<BankLoan> findByLoanAccountNumber(String loanAccountNumber);
    boolean existsByLoanAccountNumber(String loanAccountNumber);
}
