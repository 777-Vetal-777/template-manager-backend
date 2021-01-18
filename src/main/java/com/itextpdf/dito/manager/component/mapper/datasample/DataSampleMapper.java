package com.itextpdf.dito.manager.component.mapper.datasample;


import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import org.springframework.data.domain.Page;

public interface DataSampleMapper {
    DataSampleDTO map(DataSampleEntity entity);

    Page<DataSampleDTO> map(Page<DataSampleEntity> entities);
}
