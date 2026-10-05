package com.aymen.ebankservice.services;

import com.aymen.ebankservice.entities.BankAccount;
import com.aymen.ebankservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private BankAccountRepository bankAccountRepository;

    public EbankService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<BankAccount> getAllBankAccount(){
        return bankAccountRepository.findAll();
    }
    public BankAccount getBankAccountById(String id){
        return bankAccountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Bank Account Not Found"));
    }
    public BankAccount saveBankAccount(BankAccount bankAccount){
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        return bankAccountRepository.save(bankAccount);
    }
}
