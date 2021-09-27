package com.simpledatawarehouse.simpledatawarehouse.config;

import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import org.springframework.core.convert.converter.Converter;

public class StringToMetricsConverter implements Converter<String, Metrics> {
    @Override
    public Metrics convert(String source) {
        return Metrics.valueOf(source.toUpperCase());
    }
}
