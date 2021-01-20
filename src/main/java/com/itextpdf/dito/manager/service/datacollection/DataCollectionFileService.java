package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionFileService {

    Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam);

    DataCollectionFileEntity getByTemplateName(String name);

}
