package com.simpledatawarehouse.simpledatawarehouse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ResultItem {

    private Long total;
    private Double ctr;
    private LocalDate date;
    private String datasource;
    private String campaign;

    public ResultItem(String datasource, String campaign, Double ctr) {
        this.ctr = ctr;
        this.datasource = datasource;
        this.campaign = campaign;
    }

    public ResultItem(Long total, LocalDate date) {
        this.total = total;
        this.date = date;
    }

    public ResultItem(Long total, LocalDate date, String datasource) {
        this.total = total;
        this.date = date;
        this.datasource = datasource;
    }

    public ResultItem(Long total, String datasource) {
        this.total = total;
        this.datasource = datasource;
    }

    public ResultItem(Long total, String campaign, String datasource) {
        this.total = total;
        this.datasource = datasource;
        this.campaign = campaign;
    }

    public ResultItem(Long total, String campaign, LocalDate date) {
        this.total = total;
        this.date = date;
        this.campaign = campaign;
    }

    public ResultItem(Long total, String campaign, LocalDate date, String datasource) {
        this.total = total;
        this.date = date;
        this.campaign = campaign;
        this.datasource = datasource;
    }
}
