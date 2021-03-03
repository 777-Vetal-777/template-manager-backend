package com.itextpdf.dito.manager.model.template.duplicates;

import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateDuplicateNameDTO;

import java.util.List;

public interface DuplicatesList {
    List<TemplateDuplicateNameDTO> getDuplicates();

    DuplicatesList putToDuplicates(SettingType template,
                                   String templateName);

    boolean isEmpty();

}
