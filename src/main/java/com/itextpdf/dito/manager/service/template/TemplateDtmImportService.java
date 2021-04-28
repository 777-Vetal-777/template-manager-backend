package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;

import java.util.List;
import java.util.Map;

public interface TemplateDtmImportService {
    List<TemplateEntity> importTemplate(String fileName, byte[] data, String email, Map<SettingType, Map<String, TemplateImportNameModel>> settings);
}
