package com.itextpdf.dito.manager.component.mapper.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DataCollectionMapper {
    DataCollectionEntity map(DataCollectionCreateRequestDTO dto);

    DataCollectionDTO map(DataCollectionEntity entity);

    List<DataCollectionDTO> map(List<DataCollectionEntity> entities);

    Page<DataCollectionDTO> map(Page<DataCollectionEntity> entities);
}
