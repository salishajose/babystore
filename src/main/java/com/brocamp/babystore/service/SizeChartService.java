package com.brocamp.babystore.service;

import com.brocamp.babystore.model.SizeChart;

import java.util.List;

public interface SizeChartService {
    void saveOrUpdate(SizeChart sizeChart);

    List<SizeChart> findAll();
}
