package com.itextpdf.dito.manager.exception;

public class FileTypeNotSupportedException extends RuntimeException {

    private String fileType;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public FileTypeNotSupportedException(String fileType) {
        this.fileType = fileType;
    }
}
