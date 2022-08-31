package com.nschlimm.accountsms.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nschlimm.accountsms.model.Cards;
import com.nschlimm.accountsms.model.Customer;

@FeignClient("cards")
public interface CardsFeignClient {

    @RequestMapping(method=RequestMethod.POST, value="myCards", consumes ="application/json")
    List<Cards> getCardDetails(@RequestBody Customer customer);
    
}
