package com.demo.cloud;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/account-service")
public class AccountResource {

    @GetMapping
    public String hello() {
        return "Hello from Accounting";
    }
    
    
    ///Redacted DB/Service call methods
}
