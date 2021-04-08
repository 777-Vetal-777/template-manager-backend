package com.itextpdf.dito.manager.service.datasample;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataSampleService {
    DataSampleEntity create(DataCollectionEntity dataCollectionEntity, String name, String fileName, String sample, String comment, String email);

    Page<DataSampleEntity> list(Pageable pageable, Long dataCollectionId, DataSampleFilter filter, String searchParam);

    List<DataSampleEntity> list(Long dataCollectionId);

    DataSampleEntity get(String dataCollectionName, String dataSampleName);

    DataSampleEntity getByUuid(String uuid);

    DataSampleEntity setAsDefault(String dataCollectionName, String dataSampleName);

    DataSampleEntity createNewVersion(String dataCollectionName, String name, String data, String fileName, String email, String comment);

    List<DataSampleEntity> delete(String dataCollectionName, List<String> dataSamplesList);

    DataSampleEntity deleteByUuid(String uuid);

    void delete(DataCollectionEntity dataCollectionEntity);

    DataSampleEntity update(String dataCollectionName, String name, DataSampleEntity entity, String userEmail);

    Optional<DataSampleEntity> findDataSampleByTemplateId(Long templateId);

    Optional<DataSampleEntity> findDataSampleByCollectionId(Long dataCollectionId);

    DataSampleEntity getByNameAndTemplateName(String dataSampleName, String templateName);

    List<DataSampleEntity> getListByTemplateName(String templateName);
}
