package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByIsNeededException;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByNotSupportedException;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;
import com.simpledatawarehouse.simpledatawarehouse.service.MarketingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
public class MarketingController {

    private MarketingService marketingService;

    @PostMapping("/{metrics}/total/{aggregations}")
    public Number calculateAggregationNumbers(@PathVariable Metrics metrics,
                                       @PathVariable Aggregations aggregations,
                                       @RequestBody MarketingQueryRequest request) {

        if (request.getGroupBy() != null ) throw new GroupingByNotSupportedException();
        return marketingService.calculateTotalAggregationNumbers(metrics, aggregations, request);
    }

    @PostMapping(value = {"/{metrics}", "/{metrics}/{aggregations}"})
    public List<ResultItem> calculateMetrics(@PathVariable Metrics metrics,
                                             @PathVariable(required = false) Aggregations aggregations,
                                             @RequestBody MarketingQueryRequest request) {

        if (Metrics.CTR.equals(metrics) && request.getGroupBy() == null) throw new GroupingByIsNeededException();
        return marketingService.getMetricQueryResults(metrics, aggregations, request);
    }
}
