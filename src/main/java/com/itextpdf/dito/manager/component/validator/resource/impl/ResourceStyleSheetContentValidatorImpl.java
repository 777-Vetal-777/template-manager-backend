package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import io.bit3.jsass.CompilationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;

import java.nio.charset.StandardCharsets;

@Component
public class ResourceStyleSheetContentValidatorImpl implements ContentValidator {
    private static final Logger log = LogManager.getLogger(ResourceStyleSheetContentValidatorImpl.class);

    private final Compiler compiler = new Compiler();
    private final Options options = new Options();

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.STYLESHEET;
    }

    @Override
    public boolean isValid(final byte[] content) {
        boolean contentValid;
        try {
            compiler.compileString(new String(content, StandardCharsets.UTF_8), options);
            contentValid = true;
        } catch (CompilationException e) {
            log.info("Exception during validation stylesheet content: {}", e.getMessage());
            contentValid = false;
        }
        return contentValid;
    }
}
