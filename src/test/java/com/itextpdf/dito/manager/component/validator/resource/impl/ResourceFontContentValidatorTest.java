package com.itextpdf.dito.manager.component.validator.resource.impl;

import com.itextpdf.dito.manager.component.validator.resource.ContentValidator;
import com.itextpdf.dito.manager.exception.resource.InvalidResourceContentException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceFontContentValidatorTest {

    private final ContentValidator validator = new ResourceFontContentValidator();

    @ParameterizedTest
    @CsvSource({"src/test/resources/test-data/resources/fonts/Banty Bold.ttf",
            "src/test/resources/test-data/resources/fonts/Lato-Black.ttf",
            "src/test/resources/test-data/resources/fonts/Lato-BlackItalic.ttf",
            "src/test/resources/test-data/resources/fonts/Lato-Bold.ttf"})
    void testValidContent(String fileName) throws IOException {
        assertTrue(validator.isValid(Files.readAllBytes(Path.of(fileName))));
    }


    @ParameterizedTest
    @CsvSource({"src/test/resources/test-data/resources/random.png"})
    void testInvalidContent(String fileName) throws IOException {
        assertThrows(InvalidResourceContentException.class, () -> validator.isValid(Files.readAllBytes(Path.of(fileName))));
    }

}
