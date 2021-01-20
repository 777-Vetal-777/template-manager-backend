package com.itextpdf.dito.manager.component.mapper.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import org.springframework.data.domain.Page;

public interface DataCollectionMapper {
    DataCollectionEntity map(DataCollectionUpdateRequestDTO dto);

    DataCollectionDTO map(DataCollectionEntity entity);

    DataCollectionDTO mapWithFile(final DataCollectionEntity entity);

    Page<DataCollectionDTO> map(Page<DataCollectionEntity> entities);

}
