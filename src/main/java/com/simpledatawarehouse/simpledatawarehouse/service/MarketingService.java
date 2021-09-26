package com.simpledatawarehouse.simpledatawarehouse.service;

import com.simpledatawarehouse.simpledatawarehouse.controller.MarketingQueryRequest;
import com.simpledatawarehouse.simpledatawarehouse.model.CTR;
import com.simpledatawarehouse.simpledatawarehouse.model.ImpressionsOverTime;
import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class MarketingService {

    private MarketingRepository marketingRepository;

    public Long queryClicks(MarketingQueryRequest request) {
        return marketingRepository.sumClicks(request);
    }

    public List<CTR> calculateCTR(MarketingQueryRequest request) {
        return marketingRepository.calculateCTR(request);
    }

    public List<ImpressionsOverTime> calculateTotalImpressions(MarketingQueryRequest request) {
        return marketingRepository.getImpressions(request);
    }
}
