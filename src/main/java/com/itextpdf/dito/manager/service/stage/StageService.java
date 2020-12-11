package com.itextpdf.dito.manager.service.stage;

import com.itextpdf.dito.manager.entity.StageEntity;

import java.util.List;

public interface StageService {
    void delete(List<StageEntity> stages);
}
