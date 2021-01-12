package com.itextpdf.dito.manager.component.mapper.dependency;

import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DependencyMapper {
    DependencyDTO map(DependencyModel model);

    List<DependencyDTO> map(List<DependencyModel> models);

    Page<DependencyDTO> map(Page<DependencyModel> models);
}
