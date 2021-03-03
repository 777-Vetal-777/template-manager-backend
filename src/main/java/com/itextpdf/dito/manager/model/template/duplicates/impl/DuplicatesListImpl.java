package com.itextpdf.dito.manager.model.template.duplicates.impl;

import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateDuplicateNameDTO;

import java.util.ArrayList;
import java.util.List;

public class DuplicatesListImpl implements DuplicatesList {
    private final List<TemplateDuplicateNameDTO> duplicates = new ArrayList<>();

    @Override
    public List<TemplateDuplicateNameDTO> getDuplicates() {
        return duplicates;
    }

    @Override
    public DuplicatesList putToDuplicates(final SettingType template,
                                              final String templateName) {
        final TemplateDuplicateNameDTO duplicatedDataCollection = new TemplateDuplicateNameDTO();
        duplicatedDataCollection.setType(template);
        duplicatedDataCollection.setName(templateName);
        duplicates.add(duplicatedDataCollection);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return duplicates.isEmpty();
    }
}
