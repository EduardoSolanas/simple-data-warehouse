package com.simpledatawarehouse.simpledatawarehouse.config;

import com.simpledatawarehouse.simpledatawarehouse.controller.GroupByValues;
import com.simpledatawarehouse.simpledatawarehouse.controller.Metrics;
import org.springframework.core.convert.converter.Converter;

public class StringToGroupByValuesConverter implements Converter<String, GroupByValues> {
    @Override
    public GroupByValues convert(String source) {
        return GroupByValues.valueOf(source.toUpperCase());
    }
}
