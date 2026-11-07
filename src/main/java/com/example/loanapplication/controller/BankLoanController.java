package com.example.loanapplication.controller;

import com.example.loanapplication.dto.LoanRequest;
import com.example.loanapplication.entity.BankLoan;
import com.example.loanapplication.service.BankLoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class BankLoanController {

    private final BankLoanService service;

    public BankLoanController(BankLoanService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BankLoan createLoan(@Valid @RequestBody LoanRequest request) {
        return service.createLoan(request);
    }

    @GetMapping("/all")
    public List<BankLoan> getAllLoans() {
        return service.getAllLoans();
    }

    @GetMapping("/getLoan/{id}")
    public BankLoan getLoanById(@PathVariable Long id) {
        return service.getLoanById(id);
    }

    @PutMapping("/updateLoan/{id}")
    public BankLoan updateLoan(@PathVariable Long id, @Valid @RequestBody LoanRequest request) {
        return service.updateLoan(id, request);
    }

    @DeleteMapping("/deleteLoan/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLoan(@PathVariable Long id) {
        service.deleteLoan(id);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BankLoan> createLoans(
            @Valid @RequestBody List<@Valid LoanRequest> requests) {

        return service.createLoans(requests);
    }
}
