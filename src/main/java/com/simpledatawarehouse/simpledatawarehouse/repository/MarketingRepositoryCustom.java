package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.request.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;

import java.util.List;

public interface MarketingRepositoryCustom {

    Number calculateTotalAggregationNumbers(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request);

    List<ResultItem> getMetricQueryResults(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request);
}
