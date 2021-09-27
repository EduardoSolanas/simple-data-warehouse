package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;

import java.util.List;

public interface MarketingRepositoryCustom {

    List<ResultItem> calculateCTR(MarketingQueryRequest request);

    Number queryApplyingAggregator(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request);

    List<ResultItem> getMetricQueryResults(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request);
}
