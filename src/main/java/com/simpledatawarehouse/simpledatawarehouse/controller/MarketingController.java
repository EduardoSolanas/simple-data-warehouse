package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.controller.request.Aggregations;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.controller.request.Metrics;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByIsNeededForCTRException;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByNotSupportedException;
import com.simpledatawarehouse.simpledatawarehouse.model.ResultItem;
import com.simpledatawarehouse.simpledatawarehouse.service.MarketingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.simpledatawarehouse.simpledatawarehouse.controller.request.GroupByValues.CAMPAIGN;
import static com.simpledatawarehouse.simpledatawarehouse.controller.request.GroupByValues.DATASOURCE;

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
                                             @RequestBody @Valid MarketingQueryRequest request) {

        if (Metrics.CTR.equals(metrics) && (request.getGroupBy() == null ||
                !request.getGroupBy().toLowerCase().contains(DATASOURCE.name().toLowerCase()) ||
                !request.getGroupBy().toLowerCase().contains(CAMPAIGN.name().toLowerCase()))) {

            throw new GroupingByIsNeededForCTRException();
        }
        return marketingService.getMetricQueryResults(metrics, aggregations, request);
    }
}
