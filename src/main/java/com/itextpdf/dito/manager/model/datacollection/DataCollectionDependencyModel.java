package com.itextpdf.dito.manager.model.datacollection;

public class DataCollectionDependencyModel {
    private String name;
    private Long version;
    private Integer order;

    public DataCollectionDependencyModel(final String name, final Long version, final Integer sequence) {
        this.name = name;
        this.version = version;
        this.order = sequence;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}