package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.filter.resource.dependency.ResourceDependencyFilter;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceDependencyService {
    Page<DependencyModel> list(Pageable pageable, String name, ResourceTypeEnum type, ResourceDependencyFilter filter, String searchParam);

    List<DependencyModel> list(String name, ResourceTypeEnum type);

}