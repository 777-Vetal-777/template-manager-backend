package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;

public interface ResourceFileService {
    ResourceFileEntity getByUuid(String uuid);
}
