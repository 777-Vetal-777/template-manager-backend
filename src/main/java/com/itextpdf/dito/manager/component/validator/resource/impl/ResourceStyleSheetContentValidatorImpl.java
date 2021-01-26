package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.helger.css.ECSSVersion;
import com.helger.css.reader.CSSReader;
import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class ResourceStyleSheetContentValidatorImpl implements ContentValidator {
    @Override
    public boolean isValid(final byte[] content) {
        return CSSReader.isValidCSS(new String(content), ECSSVersion.CSS30);
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.STYLESHEET;
    }
}
