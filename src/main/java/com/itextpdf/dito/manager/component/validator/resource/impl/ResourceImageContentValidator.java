package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ResourceImageContentValidator implements ContentValidator {
    private static final Logger log = LogManager.getLogger(ResourceImageContentValidator.class);

    @Override
    public boolean isValid(final byte[] content) {
        boolean contentValid;
        try {
            ImageDataFactory.create(content);
            contentValid = true;
        } catch (Exception e) {
            if (!isValidSVG(content)) {
                log.info("Exception during validation image content: {}", e.getMessage());
                contentValid = false;
            } else {
                contentValid = true;
            }
        }
        return contentValid;
    }

    @Override
    public ResourceTypeEnum getType() {
        return ResourceTypeEnum.IMAGE;
    }

    private boolean isValidSVG(final byte[] content) {
        boolean svgIsValid;
        try {
            final Document document = Jsoup.parse(new ByteArrayInputStream(content), StandardCharsets.UTF_8.toString(), "");
            svgIsValid = !document.getElementsByTag("svg").isEmpty();
        } catch (Exception e) {
            log.info("Exception during validation svg image content: {}", e.getMessage());
            svgIsValid = false;
        }
        return svgIsValid;
    }
}
