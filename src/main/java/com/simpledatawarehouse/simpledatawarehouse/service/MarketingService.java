package com.simpledatawarehouse.simpledatawarehouse.service;

import com.simpledatawarehouse.simpledatawarehouse.controller.request.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MarketingService {

    private MarketingRepository marketingRepository;

    public Number calculateTotalAggregationNumbers(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        return marketingRepository.calculateTotalAggregationNumbers(metrics, aggregations, request);
    }

    public List<ResultItem> getMetricQueryResults(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        return marketingRepository.getMetricQueryResults(metrics, aggregations, request);
    }
}
