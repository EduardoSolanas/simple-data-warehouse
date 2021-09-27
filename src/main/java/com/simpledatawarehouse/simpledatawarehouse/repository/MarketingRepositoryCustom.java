package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;

import java.util.List;

public interface MarketingRepositoryCustom {

    List<CTR> calculateCTR(MarketingQueryRequest request);

    Number queryApplyingAggregator(Metrics metrics, Aggregations aggregations, MarketingQueryRequest request);

    List<ImpressionsOverTime> getMetricQueryResults(Metrics metrics, MarketingQueryRequest request);
}
