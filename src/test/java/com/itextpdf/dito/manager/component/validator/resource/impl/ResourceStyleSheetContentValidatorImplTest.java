package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceStyleSheetContentValidatorImplTest {

    private final ContentValidator validator = new ResourceStyleSheetContentValidatorImpl();

    @ParameterizedTest
    @CsvSource({".h1 { background-color: #333333; }, false",
            "body {    invalid--rule: %%; }, true",
            ".h1 { background-color: #333333; }}, true",
            "body, true",})
    void isValid(String content, Boolean exceptionExpected) {
        if (!exceptionExpected) {
            assertTrue(validator.isValid(content.getBytes()));
        } else {
            assertThrows(InvalidResourceContentException.class, () -> assertFalse(validator.isValid(content.getBytes())));
        }
    }
}