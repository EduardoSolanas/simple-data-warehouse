package com.simpledatawarehouse.simpledatawarehouse.service;

import com.simpledatawarehouse.simpledatawarehouse.model.Marketing;
import com.simpledatawarehouse.simpledatawarehouse.repository.MarketingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class MarketingDataLoadingService {

    private final MarketingRepository marketingRepository;

    private final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("MM/dd/yy");

    String csvURL;
    boolean loadInitialDataEnabled;

    public MarketingDataLoadingService(MarketingRepository marketingRepository,
                                       @Value("${marketing.data.url}") String csvURL,
                                       @Value("${marketing.data.load.enabled}")  boolean loadInitialDataEnabled
                                      ) {
        this.marketingRepository = marketingRepository;
        this.csvURL = csvURL;
        this.loadInitialDataEnabled = loadInitialDataEnabled;
    }

    public void loadMarketingData() throws IOException {

        if (loadInitialDataEnabled) {
            URL stockURL = new URL(csvURL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stockURL.openStream()));
            bufferedReader.lines().skip(1).map(mapToItem).forEach(marketingRepository::save);
        }
    }

    private final Function<String, Marketing> mapToItem = (line) -> {

        String[] p = line.split(",");

        Marketing item = new Marketing();

        item.setDatasource(p[0]);
        item.setCampaign(p[1]);
        item.setDaily(LocalDate.parse(p[2], formatter));
        item.setClicks(Long.parseLong(p[3]));
        item.setImpressions(Long.parseLong(p[4]));

        return item;
    };
}
