package com.itextpdf.dito.manager.component.auth.token.extractor.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.servlet.http.HttpServletRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpJwtExtractorTest {

    private final HttpJwtExtractor extractor = new HttpJwtExtractor();

    @ParameterizedTest
    @MethodSource("data")
    void testExtract(HttpServletRequest request, String result) {
        assertEquals(extractor.extract(request), result);
    }

    static Stream<Arguments> data() {
        HttpServletRequest requestWithToken = mock(HttpServletRequest.class);
        when(requestWithToken.getHeader("Authorization")).thenReturn("Bearer eyJhbGciO");

        HttpServletRequest requestWithoutToken = mock(HttpServletRequest.class);
        when(requestWithoutToken.getHeader("Authorization")).thenReturn("");

        return Stream.of(
                Arguments.of(
                        requestWithToken,
                        "eyJhbGciO"),
                Arguments.of(
                        requestWithoutToken,
                        null));
    }


}