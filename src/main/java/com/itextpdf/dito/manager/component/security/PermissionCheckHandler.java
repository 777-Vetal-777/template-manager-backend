package com.itextpdf.dito.manager.component.security;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import com.itextpdf.dito.manager.model.resource.ResourceModelWithRoles;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;

import java.util.List;
import java.util.Set;

public interface PermissionCheckHandler {

    Set<String> getPermissionsByDataCollection(final DataCollectionModelWithRoles data, final String email);

    Set<String> getPermissionsByDataCollection(final DataCollectionEntity data, final String email);

    Set<String> getPermissionsByResource(final ResourceModelWithRoles resource, final String email);

    Set<String> getPermissionsByResource(final ResourceEntity resource, final String email);

    Set<String> getPermissionsByTemplate(final TemplateModelWithRoles template, final String email);

    Set<String> getPermissionsByTemplate(final TemplateEntity template, final String email);

}
