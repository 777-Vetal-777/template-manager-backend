package com.itextpdf.dito.manager.component.mapper.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface DataCollectionMapper {
    DataCollectionEntity map(DataCollectionUpdateRequestDTO dto);

    DataCollectionDTO map(DataCollectionEntity entity);

    DataCollectionDTO mapWithFileWithoutRoles(DataCollectionEntity entity);

    DataCollectionDTO mapWithFile(final DataCollectionEntity entity);

    Page<DataCollectionDTO> map(Page<DataCollectionEntity> entities);

    List<DataCollectionDTO> map(Collection<DataCollectionEntity> entities);

}
