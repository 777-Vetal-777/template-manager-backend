package com.itextpdf.dito.manager.component.mapper.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface DataCollectionMapper {
    DataCollectionEntity map(DataCollectionUpdateRequestDTO dto);

    DataCollectionDTO map(DataCollectionEntity entity, String email);

    DataCollectionDTO mapWithFile(final DataCollectionEntity entity, String email);

    Page<DataCollectionDTO> map(Page<DataCollectionEntity> entities, String email);

    List<DataCollectionDTO> map(Collection<DataCollectionEntity> entities, String email);

    Page<DataCollectionDTO> mapModels(Page<DataCollectionModelWithRoles> entities, String email);

    DataCollectionDTO mapModel(DataCollectionModelWithRoles entity, String email);

}
