package com.itextpdf.dito.manager.model.dependency;

import com.itextpdf.dito.manager.dto.dependency.DependencyDirectionType;
import com.itextpdf.dito.manager.dto.dependency.DependencyType;

public interface DependencyModel {
    String getName();

    Long getVersion();

    DependencyType getDependencyType();

    String getStage();

    DependencyDirectionType getDirectionType();
}
