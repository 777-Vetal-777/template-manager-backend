package com.itextpdf.dito.manager.model.template.dtm;

public enum ItemType {
    TEMPLATE("templates"), RESOURCE("resources"), DATA_COLLECTION ("dataCollections"), DATA_SAMPLE("dataSamples");

    private final String pluralName;

    public String getPluralName() {
        return pluralName;
    }

    private ItemType(final String pluralName) {
        this.pluralName = pluralName;
    }
}
