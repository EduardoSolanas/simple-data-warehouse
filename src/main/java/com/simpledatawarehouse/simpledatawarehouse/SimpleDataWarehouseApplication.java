package com.simpledatawarehouse.simpledatawarehouse;

import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository;
import com.simpledatawarehouse.simpledatawarehouse.service.MarketingService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleDataWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDataWarehouseApplication.class, args);
    }
}
