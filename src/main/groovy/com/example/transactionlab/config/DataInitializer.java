package com.example.transactionlab.config;

import com.example.transactionlab.entity.Account;
import com.example.transactionlab.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;

    public DataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create some test accounts
        if (accountRepository.count() == 0) {
            Account account1 = new Account("ACC001", "John Doe",
                    new BigDecimal("1000.00"), Account.AccountType.SAVINGS);
            Account account2 = new Account("ACC002", "Jane Smith",
                    new BigDecimal("500.00"), Account.AccountType.CHECKING);
            Account account3 = new Account("ACC003", "Bob Johnson",
                    new BigDecimal("2500.00"), Account.AccountType.BUSINESS);

            accountRepository.save(account1);
            accountRepository.save(account2);
            accountRepository.save(account3);

            System.out.println("Test accounts created successfully!");
        }
    }
}