package com.itextpdf.dito.manager.service.datacollections.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.repository.datacollections.DataCollectionRepository;
import com.itextpdf.dito.manager.service.datacollections.DataCollectionService;
import com.itextpdf.dito.manager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Date;

@Service
public class DataCollectionServiceImpl implements DataCollectionService {

    private final DataCollectionMapper dataCollectionMapper;
    private final DataCollectionRepository dataCollectionRepository;
    private final UserService userService;

    public DataCollectionServiceImpl(final DataCollectionMapper dataCollectionMapper,
                                     final DataCollectionRepository dataCollectionRepository,
                                     final UserService userService) {
        this.dataCollectionMapper = dataCollectionMapper;
        this.dataCollectionRepository = dataCollectionRepository;
        this.userService = userService;
    }

    @Override
    public DataCollectionDTO create(final DataCollectionCreateRequestDTO requestDTO, final Principal principal) {
        final DataCollectionEntity entity = dataCollectionMapper.map(requestDTO);
        entity.setModifiedOn(new Date());
        entity.setAuthor(userService.findByEmail(principal.getName()));
        return dataCollectionMapper.map(dataCollectionRepository.save(entity));
    }

    @Override
    public Page<DataCollectionDTO> list(final Pageable pageable, final String searchParam) {
        final Page<DataCollectionEntity> result = StringUtils.isEmpty(searchParam)
                ? dataCollectionRepository.findAll(pageable)
                : dataCollectionRepository.search(pageable, searchParam);
        return dataCollectionMapper.map(result);
    }
}
