package com.example.loanapplication.controller;

import com.example.loanapplication.entity.BankLoan;
import com.example.loanapplication.service.BankLoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankLoanController.class)
class BankLoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankLoanService service;

    @Test
    void createLoan_returnsCreatedLoan() throws Exception {
        when(service.createLoan(any())).thenReturn(sampleLoan(1L, "LN-1001"));

        mockMvc.perform(post("/api/loans/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoanJson("LN-1001")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.borrowerName").value("Rahul Sharma"))
                .andExpect(jsonPath("$.loanAccountNumber").value("LN-1001"));
    }

    @Test
    void createLoan_returnsBadRequestForInvalidRequest() throws Exception {
        String invalidJson = """
                {
                  "borrowerName": "",
                  "loanAccountNumber": "LN-1001",
                  "loanType": "HOME",
                  "principalAmount": 0,
                  "annualInterestRate": 8.50,
                  "tenureMonths": 240,
                  "startDate": "2026-01-15",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/loans/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void getAllLoans_returnsLoanList() throws Exception {
        when(service.getAllLoans()).thenReturn(List.of(
                sampleLoan(1L, "LN-1001"),
                sampleLoan(2L, "LN-1002")
        ));

        mockMvc.perform(get("/api/loans/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].loanAccountNumber").value("LN-1001"))
                .andExpect(jsonPath("$[1].loanAccountNumber").value("LN-1002"));
    }

    @Test
    void getLoanById_returnsLoan() throws Exception {
        when(service.getLoanById(1L)).thenReturn(sampleLoan(1L, "LN-1001"));

        mockMvc.perform(get("/api/loans/getLoan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.loanAccountNumber").value("LN-1001"));
    }

    @Test
    void updateLoan_returnsUpdatedLoan() throws Exception {
        when(service.updateLoan(anyLong(), any())).thenReturn(sampleLoan(1L, "LN-1001"));

        mockMvc.perform(put("/api/loans/updateLoan/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoanJson("LN-1001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.loanAccountNumber").value("LN-1001"));
    }

    @Test
    void deleteLoan_returnsNoContent() throws Exception {
        doNothing().when(service).deleteLoan(1L);

        mockMvc.perform(delete("/api/loans/deleteLoan/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteLoan(1L);
    }

    @Test
    void createBulkLoans_returnsCreatedLoanList() throws Exception {
        when(service.createLoans(any())).thenReturn(List.of(
                sampleLoan(1L, "LN-1001"),
                sampleLoan(2L, "LN-1002")
        ));

        String bulkJson = """
                [
                  {
                    "borrowerName": "Rahul Sharma",
                    "loanAccountNumber": "LN-1001",
                    "loanType": "HOME",
                    "principalAmount": 2500000.00,
                    "annualInterestRate": 8.50,
                    "tenureMonths": 240,
                    "startDate": "2026-01-15",
                    "status": "ACTIVE"
                  },
                  {
                    "borrowerName": "Priya Singh",
                    "loanAccountNumber": "LN-1002",
                    "loanType": "PERSONAL",
                    "principalAmount": 500000.00,
                    "annualInterestRate": 11.50,
                    "tenureMonths": 60,
                    "startDate": "2026-02-10",
                    "status": "ACTIVE"
                  }
                ]
                """;

        mockMvc.perform(post("/api/loans/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bulkJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].loanAccountNumber").value("LN-1001"))
                .andExpect(jsonPath("$[1].loanAccountNumber").value("LN-1002"));
    }

    private String validLoanJson(String accountNumber) {
        return """
                {
                  "borrowerName": "Rahul Sharma",
                  "loanAccountNumber": "%s",
                  "loanType": "HOME",
                  "principalAmount": 2500000.00,
                  "annualInterestRate": 8.50,
                  "tenureMonths": 240,
                  "startDate": "2026-01-15",
                  "status": "ACTIVE"
                }
                """.formatted(accountNumber);
    }

    private BankLoan sampleLoan(Long id, String accountNumber) {
        BankLoan loan = new BankLoan();
        loan.setId(id);
        loan.setBorrowerName("Rahul Sharma");
        loan.setLoanAccountNumber(accountNumber);
        loan.setLoanType("HOME");
        loan.setPrincipalAmount(new BigDecimal("2500000.00"));
        loan.setAnnualInterestRate(new BigDecimal("8.50"));
        loan.setTenureMonths(240);
        loan.setStartDate(LocalDate.of(2026, 1, 15));
        loan.setStatus("ACTIVE");
        return loan;
    }
}
