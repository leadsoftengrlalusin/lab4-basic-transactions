package com.example.transactionlab.controller;

import com.example.transactionlab.entity.Account;
import com.example.transactionlab.entity.TransactionRecord;
import com.example.transactionlab.service.BankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    private final BankingService bankingService;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        String accountNumber = (String) request.get("accountNumber");
        String accountHolder = (String) request.get("accountHolder");
        BigDecimal initialBalance = new BigDecimal(request.get("initialBalance").toString());
        Account.AccountType accountType = Account.AccountType.valueOf(
                request.get("accountType").toString().toUpperCase());

        Account account = bankingService.createAccount(accountNumber, accountHolder,
                initialBalance, accountType);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(bankingService.getAllAccounts());
    }

    @GetMapping("/accounts/{accountNumber}")
//    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
    public ResponseEntity<Account> getAccount(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(bankingService.getAccount(accountNumber));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody Map<String, Object> request) {
        String fromAccount = (String) request.get("fromAccount");
        String toAccount = (String) request.get("toAccount");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        bankingService.transferMoney(fromAccount, toAccount, amount);
        return ResponseEntity.ok("Transfer completed successfully");
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
//    public ResponseEntity<List<TransactionRecord>> getTransactionHistory(@PathVariable String accountNumber) {
    public ResponseEntity<List<TransactionRecord>> getTransactionHistory(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(bankingService.getTransactionHistory(accountNumber));
    }
}