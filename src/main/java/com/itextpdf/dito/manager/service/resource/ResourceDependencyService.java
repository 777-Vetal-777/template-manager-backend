package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.model.resource.ResourceDependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceDependencyService {
    Page<ResourceDependencyModel> list(Pageable pageable, String name, ResourceTypeEnum type, DependencyFilter dependencyFilter, String searchParam);

    List<ResourceDependencyModel> list(String name, ResourceTypeEnum type);

}