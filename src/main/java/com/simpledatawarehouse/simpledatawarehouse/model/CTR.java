package com.simpledatawarehouse.simpledatawarehouse.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CTR {
    private String datasource;
    private String campaign;
    private Double ctr;
}
