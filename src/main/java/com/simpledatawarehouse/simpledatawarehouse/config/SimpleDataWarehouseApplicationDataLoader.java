package com.simpledatawarehouse.simpledatawarehouse.config;

import com.simpledatawarehouse.simpledatawarehouse.service.MarketingDataLoadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimpleDataWarehouseApplicationDataLoader implements CommandLineRunner {

    @Autowired
    MarketingDataLoadingService marketingDataLoadingService;


    @Override
    public void run(String...args) throws Exception {
        marketingDataLoadingService.loadMarketingData();
    }
}
