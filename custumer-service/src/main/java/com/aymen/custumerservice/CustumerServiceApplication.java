package com.aymen.custumerservice;

import com.aymen.custumerservice.entities.Customer;
import com.aymen.custumerservice.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CustumerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustumerServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(CustomerService customerService) {
        return args -> {
            List<String> names = List.of("Aymen","Bilal","Camil");
            names.forEach( name ->
            customerService.saveCustomer(Customer.builder()
                    .name(name)
                    .email(name+"@gmail.com")
                    .build()));
        };
    }
}