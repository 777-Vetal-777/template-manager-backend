package com.itextpdf.dito.manager.service.datacollections.impl;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollections.DataCollectionService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class DataCollectionServiceImpl implements DataCollectionService {

    private final DataCollectionRepository dataCollectionRepository;
    private final UserService userService;

    public DataCollectionServiceImpl(final DataCollectionRepository dataCollectionRepository,
                                     final UserService userService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.userService = userService;
    }

    @Override
    public DataCollectionEntity create(final DataCollectionEntity entity, final String userEmail) {
        entity.setModifiedOn(new Date());
        entity.setAuthor(userService.findByEmail(userEmail));
        return dataCollectionRepository.save(entity);
    }

    @Override
    public Page<DataCollectionEntity> list(final Pageable pageable, final String searchParam) {
        return StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.findAll(pageable)
                : dataCollectionRepository.search(pageable, searchParam);
    }
}
