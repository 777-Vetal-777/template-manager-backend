package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataCollectionService {
    DataCollectionEntity create(String name, DataCollectionType type, byte[] data, String fileName, String email);

    DataCollectionEntity createNewVersion(String name, DataCollectionType type, byte[] data, String fileName, String email, String comment);

    DataCollectionEntity rollbackVersion(String name, Long version, String email);

    Page<DataCollectionModelWithRoles> listDataCollectionModel(Pageable pageable, DataCollectionFilter filter, String searchParam);

    List<DataCollectionEntity> list(DataCollectionFilter filter, String searchParam);

    DataCollectionEntity get(String name);

    DataCollectionEntity getByTemplateName(String name);

    DataCollectionEntity getByUuid(String uuid);

    void delete(String name, String userEmail);

    DataCollectionEntity update(String name, DataCollectionEntity entity, String userEmail);

    DataCollectionEntity applyRole(String dataCollectionName, String roleName, List<String> permissions);

    DataCollectionEntity detachRole(String name, String roleName);

    DataSampleEntity create(String dataCollectionName, String name, String fileName, String sample, String comment, String email);

}
