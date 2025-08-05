package com.example.transactionlab.service;

import com.example.transactionlab.entity.Account;
import com.example.transactionlab.entity.TransactionRecord;
import com.example.transactionlab.repository.AccountRepository;
import com.example.transactionlab.repository.TransactionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankingService {

    private final AccountRepository accountRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public BankingService(AccountRepository accountRepository,
                          TransactionRecordRepository transactionRecordRepository) {
        this.accountRepository = accountRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public void transferMoney(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Find accounts with pessimistic locking to prevent race conditions
        Account fromAccount = accountRepository.findByAccountNumberWithLock(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("From account not found: " + fromAccountNumber));

        Account toAccount = accountRepository.findByAccountNumberWithLock(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("To account not found: " + toAccountNumber));

        // Check sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in account: " + fromAccountNumber);
        }

        // Perform the transfer
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save the updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record the transaction
        TransactionRecord transaction = new TransactionRecord(
                fromAccount, toAccount, amount, TransactionRecord.TransactionType.TRANSFER);
        transactionRecordRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account createAccount(String accountNumber, String accountHolder,
                                 BigDecimal initialBalance, Account.AccountType accountType) {
        // Check if account already exists
        if (accountRepository.findByAccountNumber(accountNumber).isPresent()) {
            throw new RuntimeException("Account already exists: " + accountNumber);
        }

        Account account = new Account(accountNumber, accountHolder, initialBalance, accountType);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<TransactionRecord> getTransactionHistory(String accountNumber) {
        Account account = getAccount(accountNumber);
        return transactionRecordRepository.findAll().stream()
                .filter(t -> t.getFromAccount().equals(account) || t.getToAccount().equals(account))
                .toList();
    }
}