package com.itextpdf.dito.manager.integration.editor.component.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

public interface ContentTypeHeaderMapper {
    String map(String originalName, byte[] data);
    ResourceTypeEnum getType();
}
