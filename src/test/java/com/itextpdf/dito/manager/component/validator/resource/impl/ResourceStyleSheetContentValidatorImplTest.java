package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceStyleSheetContentValidatorImplTest {

    private final ContentValidator validator = new ResourceStyleSheetContentValidatorImpl();

    @ParameterizedTest
    @CsvSource(value = {".h1 { background-color: #333333; }, true",
            "body {    invalid--rule: %%; }, false",
            ".h1 { background-color: #333333; }}, false",
            "body, false",
            "@page { @top-center { content: element(page_header2) }}, true",
            "@page { content: element(page_header2) }, true"})
    void isValid(String content, Boolean expected) {
        assertEquals(expected, validator.isValid(content.getBytes()));
    }
}