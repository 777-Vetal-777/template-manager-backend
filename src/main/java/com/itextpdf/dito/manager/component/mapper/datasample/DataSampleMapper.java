package com.itextpdf.dito.manager.component.mapper.datasample;


import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

public interface DataSampleMapper {
    DataSampleDTO map(DataSampleEntity entity);
   
}
