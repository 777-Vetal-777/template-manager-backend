package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DataCollectionService {
    DataCollectionEntity create(DataCollectionEntity collectionEntity, MultipartFile attachment, String email);

    Page<DataCollectionEntity> list(Pageable pageable, String searchParam);

    DataCollectionEntity get(String name);

    void delete(String name);

    DataCollectionEntity update(String name, DataCollectionEntity entity, String userEmail);
}
