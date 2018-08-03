package com.edge.reports.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CountByHourRepository extends JpaRepository<CountByHourEntity,String> {
    List<CountByHourEntity> findAllByDateOrderByLocation(LocalDate date);
}
