package com.itextpdf.dito.manager.service.stage.impl;

import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.repository.stage.StageRepository;
import com.itextpdf.dito.manager.service.stage.StageService;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;

    public StageServiceImpl(final StageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    @Override
    public void delete(List<StageEntity> stages) {
        stageRepository.deleteAll(stages);
    }
}
