package com.brocamp.babystore.repository;

import com.brocamp.babystore.model.SizeChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeChartRepository extends JpaRepository<SizeChart,Long> {
}
