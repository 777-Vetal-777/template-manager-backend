package com.itextpdf.dito.manager.util;

import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public final class FilesUtils {

    public static byte[] getFileBytes(final MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(file.getOriginalFilename());
        }
        return data;
    }

    private FilesUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}