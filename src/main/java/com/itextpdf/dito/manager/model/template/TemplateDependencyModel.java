package com.itextpdf.dito.manager.model.template;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;

public class TemplateDependencyModel implements DependencyModel {

    private final String name;
    private final Long version;

    public TemplateDependencyModel(final DataCollectionFileEntity entity) {
        name = entity.getDataCollection().getName();
        version = entity.getVersion();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public DependencyType getDependencyType() {
        return DependencyType.DATA_COLLECTION;
    }

    @Override
    public String getStage() {
        return null;
    }

    @Override
    public DependencyDirectionType getDirectionType() {
        return DependencyDirectionType.HARD;
    }
}
