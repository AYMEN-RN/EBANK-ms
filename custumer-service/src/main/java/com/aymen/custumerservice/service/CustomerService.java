package com.aymen.custumerservice.service;

import com.aymen.custumerservice.entities.Customer;
import com.aymen.custumerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }
    public Customer findCustomerById(Long id){
        return customerRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Customer Not Found!"));
    }

    public Customer saveCustomer(Customer customer){
        return customerRepository.save(customer);
    }


}
