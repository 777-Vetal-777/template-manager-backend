package com.itextpdf.dito.manager.component.mapper.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionVersionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionDependencyModel;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DataCollectionMapper {
    DataCollectionEntity map(DataCollectionUpdateRequestDTO dto);

    DataCollectionDTO map(DataCollectionEntity entity);

    Page<DataCollectionDTO> map(Page<DataCollectionEntity> entities);

    DependencyDTO map(DataCollectionDependencyModel model);

    List<DependencyDTO> map(List<DataCollectionDependencyModel> models);

    Page<DataCollectionVersionDTO> mapVersions(Page<DataCollectionFileEntity> entities);
}
