package com.simpledatawarehouse.simpledatawarehouse.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClicksOverTime {

    private Long impressions;
    private LocalDate date;
}
