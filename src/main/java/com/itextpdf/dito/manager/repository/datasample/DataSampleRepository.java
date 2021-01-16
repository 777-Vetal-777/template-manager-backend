package com.itextpdf.dito.manager.repository.datasample;

import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSampleRepository extends JpaRepository<DataSampleEntity, Long> {
  
}