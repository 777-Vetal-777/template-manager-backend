package com.itextpdf.dito.manager.service.instance.impl;

import com.itextpdf.dito.manager.component.builder.specification.instance.InstanceSpecificationBuilder;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.exception.instance.InstanceNotFoundException;
import com.itextpdf.dito.manager.filter.instance.InstanceFilter;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import com.itextpdf.dito.manager.service.instance.InstanceService;
import com.itextpdf.dito.manager.service.user.UserService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class InstanceServiceImpl implements InstanceService {
    private final UserService userService;
    private final InstanceRepository instanceRepository;
    private final InstanceSpecificationBuilder instanceSpecificationBuilder;

    public InstanceServiceImpl(final UserService userService, final InstanceRepository instanceRepository,
            final InstanceSpecificationBuilder instanceSpecificationBuilder) {
        this.userService = userService;
        this.instanceRepository = instanceRepository;
        this.instanceSpecificationBuilder = instanceSpecificationBuilder;
    }


    @Override
    public List<InstanceEntity> save(final List<InstanceEntity> instances, final String creatorEmail) {
        final UserEntity userEntity = userService.findByEmail(creatorEmail);

        instances.forEach(instanceEntity -> instanceEntity.setCreatedBy(userEntity));

        return instanceRepository.saveAll(instances);
    }

    @Override
    public InstanceEntity get(final String name) {
        return instanceRepository.findByName(name).orElseThrow(() -> new InstanceNotFoundException(name));
    }

    @Override
    public Page<InstanceEntity> getAll(final InstanceFilter instanceFilter, final Pageable pageable,
            final String searchParam) {
        final Specification<InstanceEntity> specification = instanceSpecificationBuilder
                .build(instanceFilter, searchParam);
        return instanceRepository.findAll(specification, pageable);
    }
}
