package com.edge.reports.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "count_by_hour")
@Getter
@Setter
@IdClass(CountByHourID.class)
public class CountByHourEntity{

    @Id
    String location;
    @Id
    LocalDateTime rangeStart;

    private LocalDate date;
    private String hourRange;
    private Integer count;

}

class CountByHourID implements Serializable{
    String location;
    LocalDateTime rangeStart;
}