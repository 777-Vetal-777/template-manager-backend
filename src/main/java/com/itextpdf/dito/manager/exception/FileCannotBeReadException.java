package com.itextpdf.dito.manager.exception;

public class FileCannotBeReadException extends RuntimeException {

    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileCannotBeReadException(String fileName) {
        this.fileName = fileName;
    }
}
