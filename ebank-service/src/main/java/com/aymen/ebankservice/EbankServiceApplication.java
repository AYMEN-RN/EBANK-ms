package com.aymen.ebankservice;

import com.aymen.ebankservice.entities.BankAccount;
import com.aymen.ebankservice.services.EbankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EbankServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(EbankServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(EbankService ebankService) {
        return args -> {
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j < 5; j++) {
                    ebankService.saveBankAccount(BankAccount.builder()
                            .accType(Math.random()>0.5? "CURRENT_ACCOUNT" : "SAVING_ACCOUNT")
                            .balance(1000+ Math.random()*1000)
                            .customerId(i)
                            .build());
                }
            }
        };
    }
}
