package com.itextpdf.dito.manager.service.datacollections;

import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionService {
    DataCollectionEntity create(DataCollectionEntity entity, String userEmail);

    Page<DataCollectionEntity> list(Pageable pageable, String searchParam);
}
