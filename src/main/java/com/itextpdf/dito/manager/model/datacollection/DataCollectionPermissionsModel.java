package com.itextpdf.dito.manager.model.datacollection;

public interface DataCollectionPermissionsModel {
    String getName();
    String getType();
    Boolean getE6_US34_EDIT_DATA_COLLECTION_METADATA();
    Boolean getE6_US35_CREATE_A_NEW_VERSION_OF_DATA_COLLECTION_USING_JSON();
    Boolean getE6_US37_ROLL_BACK_OF_THE_DATA_COLLECTION();
    Boolean getE6_US38_DELETE_DATA_COLLECTION();
    Boolean getE7_US44_CREATE_NEW_DATA_SAMPLE_BASED_ON_JSON_FILE();
    Boolean getE7_US47_EDIT_SAMPLE_METADATA();
    Boolean getE7_US48_CREATE_NEW_VERSION_OF_DATA_SAMPLE();
    Boolean getE7_US50_DELETE_DATA_SAMPLE();
}
