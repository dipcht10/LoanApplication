package com.example.loanapplication.service;

import com.example.loanapplication.dto.LoanRequest;
import com.example.loanapplication.entity.BankLoan;
import com.example.loanapplication.exception.ResourceNotFoundException;
import com.example.loanapplication.repository.BankLoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankLoanService {

    private final BankLoanRepository repository;

    public BankLoanService(BankLoanRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BankLoan> getAllLoans() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public BankLoan getLoanById(Long id) {
        return findLoan(id);
    }

    public BankLoan createLoan(LoanRequest request) {
        if (repository.existsByLoanAccountNumber(request.loanAccountNumber())) {
            throw new IllegalArgumentException("Loan account number already exists");
        }
        BankLoan loan = new BankLoan();
        copyRequest(request, loan);
        return repository.save(loan);
    }

    public BankLoan updateLoan(Long id, LoanRequest request) {
        BankLoan loan = findLoan(id);
        repository.findByLoanAccountNumber(request.loanAccountNumber())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new IllegalArgumentException("Loan account number already exists"); });
        copyRequest(request, loan);
        return repository.save(loan);
    }

    public void deleteLoan(Long id) {
        repository.delete(findLoan(id));
    }

    private BankLoan findLoan(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
    }

    private void copyRequest(LoanRequest request, BankLoan loan) {
        loan.setBorrowerName(request.borrowerName());
        loan.setLoanAccountNumber(request.loanAccountNumber());
        loan.setLoanType(request.loanType());
        loan.setPrincipalAmount(request.principalAmount());
        loan.setAnnualInterestRate(request.annualInterestRate());
        loan.setTenureMonths(request.tenureMonths());
        loan.setStartDate(request.startDate());
        loan.setStatus(request.status());
    }

    public List<BankLoan> createLoans(List<LoanRequest> requests) {
        List<BankLoan> loans = requests.stream()
                .map(request -> {
                    BankLoan loan = new BankLoan();
                    copyRequest(request, loan);
                    return loan;
                })
                .toList();
        return repository.saveAll(loans);
    }
}
