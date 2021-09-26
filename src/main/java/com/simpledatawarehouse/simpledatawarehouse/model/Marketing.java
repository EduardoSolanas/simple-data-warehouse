package com.simpledatawarehouse.simpledatawarehouse.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class Marketing {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String datasource;
    private String campaign;
    private LocalDate daily;
    private Long clicks;
    private Long impressions;
}
