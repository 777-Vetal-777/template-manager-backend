package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateVersionsService {
    Page<TemplateFileEntity> list(Pageable pageable, String name, VersionFilter filter, String searchParam);

}
