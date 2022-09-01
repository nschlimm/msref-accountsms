package com.nschlimm.accountsms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nschlimm.accountsms.config.AccountsServiceConfig;
import com.nschlimm.accountsms.model.Accounts;
import com.nschlimm.accountsms.model.Cards;
import com.nschlimm.accountsms.model.Customer;
import com.nschlimm.accountsms.model.CustomerDetails;
import com.nschlimm.accountsms.model.Loans;
import com.nschlimm.accountsms.model.Properties;
import com.nschlimm.accountsms.repository.AccountsRepository;
import com.nschlimm.accountsms.service.client.CardsFeignClient;
import com.nschlimm.accountsms.service.client.LoansFeignClient;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class AccountsController {

    @Autowired
    private AccountsServiceConfig config;

    @Autowired
    private AccountsRepository accountsRepository;
    
    @Autowired
    private LoansFeignClient loansFeignClient;
    
    @Autowired
    private CardsFeignClient cardsFeignClient;

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
    
    @PostMapping("/myCustomerDetails")
//    @CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod = "myCustomerDetailsFallback")
    @Retry(name = "retryForCustomerDetails",fallbackMethod = "myCustomerDetailsFallback")  
    public CustomerDetails getCustomerDetails(@RequestHeader("schlimmbank-correlation-id") String correlationId,@RequestBody Customer customer) {
        
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(correlationId,customer);
        
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);
        return customerDetails;
        
    }
    
    @SuppressWarnings("unused")
    private CustomerDetails myCustomerDetailsFallback(@RequestHeader("schlimmbank-correlation-id") String correlationId,@RequestBody Customer customer,Throwable throwable) {
        
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId, customer);
        
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        return customerDetails;

    }
    
    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
       return "Hello, Welcome to Schlimm Bank";
    }
    
    @SuppressWarnings("unused")
    private String sayHelloFallback(Throwable t) {
        return "Hi, Welcome to Schlimm Bank";
    }
}
