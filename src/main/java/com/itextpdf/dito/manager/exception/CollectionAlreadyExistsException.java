package com.itextpdf.dito.manager.exception;

public class CollectionAlreadyExistsException extends RuntimeException {

    private String collectionName;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public CollectionAlreadyExistsException(String collectionName) {
        this.collectionName = collectionName;
    }
}
