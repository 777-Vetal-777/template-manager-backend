package com.itextpdf.dito.manager.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class AbstractController {
    private static final Logger log = LogManager.getLogger(AbstractController.class);

    private final Decoder decoder = Base64.getUrlDecoder();

    protected String decodeBase64(final String data) {
        return new String(decoder.decode(data));
    }

    protected String inputStreamToString(final InputStream inputStream) {
        final StringBuilder result = new StringBuilder();

        try (final Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                result.append((char) c);
            }
        } catch (IOException e) {
            log.error(e);
        }

        return result.toString();
    }
}