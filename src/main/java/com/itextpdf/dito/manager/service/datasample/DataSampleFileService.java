package com.itextpdf.dito.manager.service.datasample;

import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataSampleFileService {
    Page<FileVersionModel> list(Pageable pageable, String name, VersionFilter versionFilter, String searchParam);
}