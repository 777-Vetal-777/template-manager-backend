package com.itextpdf.dito.manager.integration.editor.component.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.springframework.http.HttpHeaders;

public interface ResponseHeadersUpdater {
    HttpHeaders updateHeaders(ResourceFileEntity entity, ResourceTypeEnum type, HttpHeaders headers);
}