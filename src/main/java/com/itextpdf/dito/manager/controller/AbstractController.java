package com.itextpdf.dito.manager.controller;

import com.itextpdf.dito.manager.exception.datacollection.EmptyDataCollectionFileException;
import com.itextpdf.dito.manager.exception.datacollection.UnreadableDataCollectionException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public abstract class AbstractController {
    protected byte[] getBytesFromMultipart(final MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throwEmptyFileException();
        }
        byte[] data = null;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throwUnreadableFileException(multipartFile.getOriginalFilename());
        }
        return data;
    }

    protected void throwEmptyFileException() {
        throw new EmptyDataCollectionFileException();
    }

    protected void throwUnreadableFileException(String fileName) {
        throw new UnreadableDataCollectionException(fileName);
    }
}