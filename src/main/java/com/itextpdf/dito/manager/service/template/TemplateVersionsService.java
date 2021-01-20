package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateVersionsService {
    Page<FileVersionModel> list(Pageable pageable, String name, VersionFilter filter, String searchParam);

}
