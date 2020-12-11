package com.itextpdf.dito.manager.service.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstanceService {
    List<InstanceEntity> save(List<InstanceEntity> instances, String creatorEmail);

    InstanceEntity get(String name);

    Page<InstanceEntity> getAll(InstanceFilter instanceFilter, Pageable pageable, String searchParam);
}
