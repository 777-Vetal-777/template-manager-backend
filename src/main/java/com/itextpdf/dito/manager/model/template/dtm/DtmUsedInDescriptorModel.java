package com.itextpdf.dito.manager.model.template.dtm;

public class DtmUsedInDescriptorModel {
    private String type;
    private Long id;
    private Long version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "DtmUsedInDescriptorModel{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", version=" + version +
                '}';
    }
}
