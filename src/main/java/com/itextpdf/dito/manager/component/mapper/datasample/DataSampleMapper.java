package com.itextpdf.dito.manager.component.mapper.datasample;


import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DataSampleMapper {
    DataSampleDTO map(DataSampleEntity entity);

    DataSampleDTO mapWithFile(DataSampleEntity entity);

    Page<DataSampleDTO> map(Page<DataSampleEntity> entities);
    
    DataSampleEntity map(DataSampleUpdateRequestDTO dto);

    List<DataSampleDTO> map(List<DataSampleEntity> entities);
}
