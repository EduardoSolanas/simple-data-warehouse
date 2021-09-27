package com.simpledatawarehouse.simpledatawarehouse.service;

import com.simpledatawarehouse.simpledatawarehouse.controller.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MarketingService {

    private MarketingRepository marketingRepository;

    public Number calculateAggregationNumbers(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request) {
        return marketingRepository.queryApplyingAggregator(metrics, aggregations, request);
    }

    public List<CTR> calculateCTR(MarketingQueryRequest request) {
        return marketingRepository.calculateCTR(request);
    }

    public List<ImpressionsOverTime> getMetricQueryResults(Metrics metrics, MarketingQueryRequest request) {
        return marketingRepository.getMetricQueryResults(metrics, request);
    }
}
