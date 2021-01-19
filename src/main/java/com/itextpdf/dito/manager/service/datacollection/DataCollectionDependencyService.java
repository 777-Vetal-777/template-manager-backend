package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataCollectionDependencyService {

    Page<DependencyModel> list(Pageable pageable, String name, DependencyFilter filter, String searchParam);

    List<DependencyModel> list(final String name);

}
