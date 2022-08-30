package com.nschlimm.accountsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@RefreshScope
@ComponentScans({ @ComponentScan("com.nschlimm.accountsms.controller") })
@EnableJpaRepositories("com.nschlimm.accountsms.repository")
@EntityScan("com.nschlimm.accountsms.model")
public class AccountsmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsmsApplication.class, args);
    }

}
