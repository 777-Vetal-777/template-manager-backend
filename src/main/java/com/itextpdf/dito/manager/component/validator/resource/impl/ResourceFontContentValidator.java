package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ResourceFontContentValidator implements ContentValidator {
    @Override
    public boolean isValid(final byte[] content) {
        try (final InputStream byteStream = new ByteArrayInputStream(content)) {
            Font.createFont(Font.TRUETYPE_FONT, byteStream);
            return true;
        } catch (IOException | FontFormatException e) {
            throw new InvalidResourceContentException(e);
        }
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.FONT;
    }
}
