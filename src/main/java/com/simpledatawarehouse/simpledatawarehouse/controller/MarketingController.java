package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import com.simpledatawarehouse.simpledatawarehouse.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MarketingController {

    @Autowired
    MarketingService marketingService;


    @PostMapping("/marketing/clicks/total")
    public Long calculateTotalClicks(@RequestBody MarketingQueryRequest request) {
        return marketingService.queryClicks(request);
    }

    @PostMapping("/marketing/impressions/total")
    public List<ImpressionsOverTime> calculateTotalImpressions(@RequestBody MarketingQueryRequest request) {
        return marketingService.calculateTotalImpressions(request);
    }

    @PostMapping("/marketing/ctr")
    public List<CTR> calculateCTR(@RequestBody MarketingQueryRequest request) {
        return marketingService.calculateCTR(request);
    }
}
