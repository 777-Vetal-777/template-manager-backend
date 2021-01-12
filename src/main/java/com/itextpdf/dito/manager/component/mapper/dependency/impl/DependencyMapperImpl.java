package com.itextpdf.dito.manager.component.mapper.dependency.impl;

import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DependencyMapperImpl implements DependencyMapper {

    @Override
    public DependencyDTO map(final DependencyModel model) {
        final DependencyDTO dependency = new DependencyDTO();
        dependency.setName(model.getName());
        dependency.setDependencyType(model.getDependencyType());
        dependency.setStage(model.getStage());
        dependency.setDirectionType(model.getDirectionType());
        dependency.setVersion(model.getVersion());
        return dependency;
    }

    @Override
    public List<DependencyDTO> map(final List<DependencyModel> models) {
        return models.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Page<DependencyDTO> map(final Page<DependencyModel> models) {
        return models.map(this::map);
    }
}
