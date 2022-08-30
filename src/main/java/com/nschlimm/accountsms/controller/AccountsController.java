package com.nschlimm.accountsms.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nschlimm.accountsms.config.AccountsServiceConfig;
import com.nschlimm.accountsms.model.Accounts;
import com.nschlimm.accountsms.model.Customer;
import com.nschlimm.accountsms.model.Properties;
import com.nschlimm.accountsms.repository.AccountsRepository;

@RestController
public class AccountsController {

    @Autowired
    private AccountsServiceConfig config;

    @Autowired
    private AccountsRepository accountsRepository;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        Optional<Accounts> accounts = Optional
                .ofNullable(accountsRepository.findByCustomerId(customer.getCustomerId()));
        return accounts.orElse(new Accounts());
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(config.getMsg(), config.getBuildVersion(), config.getMailDetails(),
                config.getActiveBranches());
        String jsonStr = ow.writeValueAsString(properties);
        return jsonStr;
    }

}
