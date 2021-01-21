package com.itextpdf.dito.manager.controller;

import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractController {
    private final Decoder decoder = Base64.getUrlDecoder();

    protected String decodeBase64(final String data) {
        return new String(decoder.decode(data));
    }

    public static byte[] getFileBytes(final MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(file.getOriginalFilename());
        }
        return data;
    }
}
