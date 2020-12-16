package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;

import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionService {
    DataCollectionEntity create(String name, DataCollectionType type, byte[] data, String fileName,
                                String email);

    Page<DataCollectionEntity> list(Pageable pageable, DataCollectionFilter filter, String searchParam);

    DataCollectionEntity get(String name);

    void delete(String name);

    DataCollectionEntity update(String name, DataCollectionEntity entity, String userEmail);
}
