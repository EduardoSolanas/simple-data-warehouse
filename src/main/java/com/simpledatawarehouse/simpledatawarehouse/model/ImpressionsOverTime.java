package com.simpledatawarehouse.simpledatawarehouse.model;

import lombok.Data;
import java.time.LocalDate;


@Data
public class ImpressionsOverTime {

    private final Long impressions;
    private final LocalDate date;
}
