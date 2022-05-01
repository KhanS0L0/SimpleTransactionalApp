package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SimpleTransactionalAppApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SimpleTransactionalAppApplication.class, args);
//        StoreService storeService = (StoreService) context.getBean(StoreService.class);
//        System.out.println(storeService.getClass().getName());
    }

}
