package com.aymen.ebankservice.services;

import com.aymen.ebankservice.entities.BankAccount;
import com.aymen.ebankservice.feign.CustomerRestClient;
import com.aymen.ebankservice.model.Customer;
import com.aymen.ebankservice.repository.BankAccountRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private BankAccountRepository bankAccountRepository;
    private CustomerRestClient customerRestClient;

    public EbankService(BankAccountRepository bankAccountRepository, CustomerRestClient customerRestClient) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRestClient = customerRestClient;
    }

    @McpTool(description = "Get All Bank Accounts")
    public List<BankAccount> getAllBankAccount(){
        return bankAccountRepository.findAll();
    }
    @McpTool(description = "Get Account by Id")
    public BankAccount getBankAccountById(@McpToolParam(description = "Bank Account ID") String id){
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Bank Account Not Found"));
        bankAccount.setCustomer(customerRestClient.getCustomerById(bankAccount.getCustomerId()));
        return bankAccount;
    }

    @McpTool(description = "Save new Account")
    public BankAccount saveBankAccount(@McpToolParam(description = "The BankAccount to save (balance, type, customerId)") BankAccount bankAccount){
        try {
            Customer customer = customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return bankAccountRepository.save(bankAccount);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }}
}
