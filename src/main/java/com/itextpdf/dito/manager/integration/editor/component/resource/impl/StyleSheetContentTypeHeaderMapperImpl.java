package com.itextpdf.dito.manager.integration.editor.component.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.integration.editor.component.resource.ContentTypeHeaderMapper;
import org.springframework.stereotype.Component;

@Component
public class StyleSheetContentTypeHeaderMapperImpl implements ContentTypeHeaderMapper {
    @Override
    public String map(final String originalName, final byte[] data) {
        return "text/css; charset=utf-8";
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.STYLESHEET;
    }
}
