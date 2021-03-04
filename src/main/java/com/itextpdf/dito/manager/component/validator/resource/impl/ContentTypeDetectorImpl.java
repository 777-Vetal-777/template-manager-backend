package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentTypeDetector;
import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContentTypeDetectorImpl implements ContentTypeDetector {

    private final Map<ResourceTypeEnum, ContentValidator> contentValidators;

    public ContentTypeDetectorImpl(final List<ContentValidator> contentValidators) {
        this.contentValidators = contentValidators.stream()
                .filter(validator -> !ResourceTypeEnum.FONT.equals(validator.getType())) /* for now we should exclude font type validators */
                .collect(Collectors.toMap(ContentValidator::getType, Function.identity()));
    }

    @Override
    public ResourceTypeEnum detectType(final byte[] data) {
        ResourceTypeEnum type = null;

        for (Map.Entry<ResourceTypeEnum, ContentValidator> entry : contentValidators.entrySet()) {
            if (entry.getValue().isValid(data)) {
                type = entry.getKey();
            }
        }

        return type;
    }
}
