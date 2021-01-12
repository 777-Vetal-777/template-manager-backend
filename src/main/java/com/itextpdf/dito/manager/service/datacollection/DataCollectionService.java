package com.itextpdf.dito.manager.service.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;

import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DataCollectionService {
    DataCollectionEntity create(String name, DataCollectionType type, byte[] data, String fileName,
                                String email);

    Page<DataCollectionEntity> list(Pageable pageable, DataCollectionFilter filter, String searchParam);

    DataCollectionEntity get(String name);

    DataCollectionEntity getByTemplateName(String name);

    void delete(String name);

    DataCollectionEntity update(String name, DataCollectionEntity entity, String userEmail);

    Page<RoleEntity> getRoles(Pageable pageable, String name, RoleFilter filter);

    DataCollectionEntity applyRole(String resourceName, String roleName, List<String> permissions);

    DataCollectionEntity detachRole(String name, String roleName);

}
