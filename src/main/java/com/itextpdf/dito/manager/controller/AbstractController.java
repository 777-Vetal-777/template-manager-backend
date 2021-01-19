package com.itextpdf.dito.manager.controller;

import java.util.Base64;
import java.util.Base64.Decoder;

public abstract class AbstractController {
    private final Decoder decoder = Base64.getUrlDecoder();

    protected String decodeBase64(final String data) {
        return new String(decoder.decode(data));
    }
}
