package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.service.MarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MarketingController {

    @Autowired
    MarketingService marketingService;


    @PostMapping("/marketing/{metrics}/{aggregations}")
    public Number calculateAggregationNumbers(@PathVariable Metrics metrics,
                                       @PathVariable Aggregations aggregations,
                                       @RequestBody MarketingQueryRequest request) {
        return marketingService.calculateAggregationNumbers(metrics, aggregations, request);
    }

    @PostMapping("/marketing/{metrics}")
    public List<ImpressionsOverTime> calculateMetrics(@PathVariable Metrics metrics, @RequestBody MarketingQueryRequest request) {
        return marketingService.getMetricQueryResults(metrics, request);
    }

    @PostMapping("/marketing/ctr")
    public List<CTR> calculateCTR(@RequestBody MarketingQueryRequest request) {
        return marketingService.calculateCTR(request);
    }
}
