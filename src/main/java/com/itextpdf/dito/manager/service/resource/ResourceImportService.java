package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.editor.server.common.elements.urigenerator.ResourceUriGenerator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;

import java.io.IOException;
import java.util.Map;

public interface ResourceImportService {

    ResourceEntity importResource(byte[] data, ResourceTypeEnum type, String name, String additionalName, String email, TemplateImportNameModel nameModel) throws IOException;

    ResourceUriGenerator getResourceUriGenerator(String fileName, String email, Map<SettingType, Map<String, TemplateImportNameModel>> settings, DuplicatesList duplicatesList);
}
