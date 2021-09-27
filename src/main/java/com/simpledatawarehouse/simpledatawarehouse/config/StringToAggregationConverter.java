package com.simpledatawarehouse.simpledatawarehouse.config;

import com.simpledatawarehouse.simpledatawarehouse.controller.Aggregations;
import org.springframework.core.convert.converter.Converter;

public class StringToAggregationConverter implements Converter<String, Aggregations> {
    @Override
    public Aggregations convert(String source) {
        return Aggregations.valueOf(source.toUpperCase());
    }
}
