package com.itextpdf.dito.manager.service.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;

import java.util.List;

import com.itextpdf.dito.manager.model.instance.InstanceSummaryStatusModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstanceService {
    List<InstanceEntity> save(List<InstanceEntity> instances, String creatorEmail);

    InstanceEntity save(InstanceEntity instance, String creatorEmail);

    InstanceEntity get(String name);

    List<InstanceEntity> getAll();

    InstanceSummaryStatusModel getSummary();

    Page<InstanceEntity> getAll(InstanceFilter instanceFilter, Pageable pageable, String searchParam);

    void forget(String name);

    InstanceEntity update(String name, InstanceEntity instanceEntity);
}
