package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.io.font.FontProgramFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ResourceFontContentValidator implements ContentValidator {
    private static final Logger log = LogManager.getLogger(ResourceFontContentValidator.class);

    @Override
    public boolean isValid(final byte[] content) {
        boolean contentValid;
        try {
            contentValid = FontProgramFactory.createFont(content, false) instanceof com.itextpdf.io.font.TrueTypeFont;
        } catch (java.io.IOException | com.itextpdf.io.IOException e) {
            log.info("Exception during validation font content: {}", e.getMessage());
            contentValid = false;
        }
        return contentValid;
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.FONT;
    }
}
