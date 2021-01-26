package com.itextpdf.dito.manager.component.validator.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

public interface ContentValidator {
    boolean isValid(byte[] content);

    ResourceTypeEnum getType();
}
