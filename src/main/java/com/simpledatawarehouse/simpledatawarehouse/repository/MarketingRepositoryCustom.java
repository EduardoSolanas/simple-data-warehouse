package com.simpledatawarehouse.simpledatawarehouse.repository;

import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MarketingRepositoryCustom {

    List<CTR> calculateCTR(MarketingQueryRequest request);

    Long sumClicks(MarketingQueryRequest request);

    List<ImpressionsOverTime> getImpressions(MarketingQueryRequest request);
}
