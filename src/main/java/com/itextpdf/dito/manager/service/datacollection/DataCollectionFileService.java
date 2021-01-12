package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionFileService {

    Page<DataCollectionFileEntity> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam);

    DataCollectionFileEntity getByTemplateName(String name);

}
