package com.itextpdf.dito.manager.integration.editor.component.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.integration.editor.component.resource.ContentTypeHeaderMapper;
import org.springframework.stereotype.Component;

@Component
public class FontContentTypeHeaderMapperImpl implements ContentTypeHeaderMapper {

    @Override
    public String map(final String originalName, final byte[] data) {
        return "font/ttf";
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.FONT;
    }
}
