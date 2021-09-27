package com.simpledatawarehouse.simpledatawarehouse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDate;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpressionsOverTime {

    private final Long total;
    private final LocalDate date;
}
