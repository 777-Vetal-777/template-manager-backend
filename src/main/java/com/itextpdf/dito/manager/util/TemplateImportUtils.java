package com.itextpdf.dito.manager.util;

import com.itextpdf.dito.editor.server.common.core.stream.Streamable;

import java.io.IOException;
import java.io.InputStream;

public final class TemplateImportUtils {

    public static byte[] readStreamable(final Streamable streamable) throws IOException {
        try (final InputStream stream = streamable.openStream()) {
            return stream.readAllBytes();
        }
    }

    private TemplateImportUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
