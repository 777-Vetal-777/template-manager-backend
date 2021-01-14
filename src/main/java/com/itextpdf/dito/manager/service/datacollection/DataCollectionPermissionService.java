package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataCollectionPermissionService {
    Page<DataCollectionPermissionsModel> getRoles(Pageable pageable, String name, DataCollectionPermissionFilter filter, String search);
}
