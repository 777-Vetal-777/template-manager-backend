package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplatePermissionService {
    Page<TemplatePermissionsModel> getRoles(Pageable pageable, String name, TemplatePermissionFilter filter, String search);
}
