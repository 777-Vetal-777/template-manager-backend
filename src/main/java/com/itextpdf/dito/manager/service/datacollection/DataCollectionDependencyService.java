package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.filter.datacollection.DataCollectionDependencyFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionDependencyModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionDependencyService {

    Page<DataCollectionDependencyModel> list(Pageable pageable, String name, DataCollectionDependencyFilter filter, String searchParam);

}
