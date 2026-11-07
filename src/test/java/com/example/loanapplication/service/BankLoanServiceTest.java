package com.example.loanapplication.service;

import com.example.loanapplication.dto.LoanRequest;
import com.example.loanapplication.entity.BankLoan;
import com.example.loanapplication.exception.ResourceNotFoundException;
import com.example.loanapplication.repository.BankLoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankLoanServiceTest {

    @Mock
    private BankLoanRepository repository;

    @InjectMocks
    private BankLoanService service;

    private LoanRequest request;

    @BeforeEach
    void setUp() {
        request = new LoanRequest(
                "Rahul Sharma",
                "LN-1001",
                "HOME",
                new BigDecimal("2500000.00"),
                new BigDecimal("8.50"),
                240,
                LocalDate.of(2026, 1, 15),
                "ACTIVE"
        );
    }

    @Test
    void getAllLoans_returnsAllLoans() {
        BankLoan loan = loanWithId(1L);
        when(repository.findAll()).thenReturn(List.of(loan));

        List<BankLoan> result = service.getAllLoans();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(repository).findAll();
    }

    @Test
    void getLoanById_returnsLoanWhenFound() {
        BankLoan loan = loanWithId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(loan));

        BankLoan result = service.getLoanById(1L);

        assertEquals(1L, result.getId());
        verify(repository).findById(1L);
    }

    @Test
    void getLoanById_throwsNotFoundWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getLoanById(99L)
        );

        assertEquals("Loan not found with id: 99", exception.getMessage());
        verify(repository).findById(99L);
    }

    @Test
    void createLoan_savesNewLoan() {
        when(repository.existsByLoanAccountNumber("LN-1001")).thenReturn(false);
        when(repository.save(any(BankLoan.class))).thenAnswer(invocation -> {
            BankLoan saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        BankLoan result = service.createLoan(request);

        assertEquals(1L, result.getId());
        assertEquals("Rahul Sharma", result.getBorrowerName());
        assertEquals("LN-1001", result.getLoanAccountNumber());
        assertEquals(new BigDecimal("2500000.00"), result.getPrincipalAmount());
        verify(repository).existsByLoanAccountNumber("LN-1001");
        verify(repository).save(any(BankLoan.class));
    }

    @Test
    void createLoan_throwsWhenAccountNumberAlreadyExists() {
        when(repository.existsByLoanAccountNumber("LN-1001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createLoan(request)
        );

        assertEquals("Loan account number already exists", exception.getMessage());
        verify(repository, never()).save(any(BankLoan.class));
    }

    @Test
    void updateLoan_updatesExistingLoan() {
        BankLoan existing = loanWithId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByLoanAccountNumber("LN-1001")).thenReturn(Optional.of(existing));
        when(repository.save(any(BankLoan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BankLoan result = service.updateLoan(1L, request);

        assertEquals("Rahul Sharma", result.getBorrowerName());
        assertEquals("LN-1001", result.getLoanAccountNumber());
        verify(repository).save(existing);
    }

    @Test
    void updateLoan_throwsNotFoundWhenLoanDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateLoan(99L, request));

        verify(repository, never()).save(any(BankLoan.class));
    }

    @Test
    void updateLoan_throwsWhenAnotherLoanUsesAccountNumber() {
        BankLoan existing = loanWithId(1L);
        BankLoan another = loanWithId(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.findByLoanAccountNumber("LN-1001")).thenReturn(Optional.of(another));

        assertThrows(IllegalArgumentException.class, () -> service.updateLoan(1L, request));

        verify(repository, never()).save(any(BankLoan.class));
    }

    @Test
    void deleteLoan_deletesExistingLoan() {
        BankLoan loan = loanWithId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(loan));

        service.deleteLoan(1L);

        verify(repository).delete(loan);
    }

    @Test
    void deleteLoan_throwsNotFoundWhenLoanDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteLoan(99L));

        verify(repository, never()).delete(any(BankLoan.class));
    }

    @Test
    void createLoans_savesListOfLoans() {
        List<LoanRequest> requests = List.of(request,
                new LoanRequest("Priya Singh", "LN-1002", "PERSONAL",
                        new BigDecimal("500000.00"), new BigDecimal("11.50"),
                        60, LocalDate.of(2026, 2, 10), "ACTIVE"));

        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<BankLoan> result = service.createLoans(requests);

        assertEquals(2, result.size());
        assertEquals("LN-1001", result.get(0).getLoanAccountNumber());
        assertEquals("LN-1002", result.get(1).getLoanAccountNumber());
        verify(repository).saveAll(anyList());
    }

    private BankLoan loanWithId(Long id) {
        BankLoan loan = new BankLoan();
        loan.setId(id);
        loan.setBorrowerName("Rahul Sharma");
        loan.setLoanAccountNumber("LN-" + id);
        loan.setLoanType("HOME");
        loan.setPrincipalAmount(new BigDecimal("2500000.00"));
        loan.setAnnualInterestRate(new BigDecimal("8.50"));
        loan.setTenureMonths(240);
        loan.setStartDate(LocalDate.of(2026, 1, 15));
        loan.setStatus("ACTIVE");
        return loan;
    }
}
