package com.itextpdf.dito.manager.model.datacollection;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;

import static com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType.HARD;
import static com.itextpdf.dito.manager.dto.dependency.DependencyType.TEMPLATE;

public class DataCollectionDependencyModel implements DependencyModel {
    private final String name;
    private final Long version;
    private final String stage;

    public DataCollectionDependencyModel(final String name, final Long version, final String stageName) {
        this.name = name;
        this.version = version;
        this.stage = stageName;
    }

    public String getName() {
        return name;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public DependencyType getDependencyType() {
        return TEMPLATE;
    }

    @Override
    public String getStage() {
        return stage;
    }

    @Override
    public DependencyDirectionType getDirectionType() {
        return HARD;
    }
}