package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import com.itextpdf.io.font.FontProgramFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourceFontContentValidator implements ContentValidator {
    @Override
    public boolean isValid(final byte[] content) {
        try {
            return FontProgramFactory.createFont(content, false) instanceof com.itextpdf.io.font.TrueTypeFont;
        } catch (java.io.IOException | com.itextpdf.io.IOException e) {
            throw new InvalidResourceContentException(e);
        }
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.FONT;
    }
}
