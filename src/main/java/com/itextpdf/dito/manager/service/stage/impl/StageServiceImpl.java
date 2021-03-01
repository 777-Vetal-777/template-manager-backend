package com.itextpdf.dito.manager.service.stage.impl;

import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.service.stage.StageService;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class StageServiceImpl implements StageService {
    private static final Logger log = LogManager.getLogger(StageServiceImpl.class);
    private final StageRepository stageRepository;

    public StageServiceImpl(final StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public void delete(final List<StageEntity> stages) {
        log.info("Delete stages: {} was started", stages);
        stageRepository.deleteAll(stages);
        log.info("Delete stages: {} was finished successfully", stages);
    }
}
