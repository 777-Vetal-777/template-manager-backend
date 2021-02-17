package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourceImageContentValidator implements ContentValidator {
    @Override
    public boolean isValid(final byte[] content) {
        try {
            ImageDataFactory.create(content);
            return true;
        } catch (Exception e) {
            throw new InvalidResourceContentException(e);
        }
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.IMAGE;
    }
}
