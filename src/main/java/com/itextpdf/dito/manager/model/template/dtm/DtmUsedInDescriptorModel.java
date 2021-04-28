package com.itextpdf.dito.manager.model.template.dtm;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DtmUsedInDescriptorModel that = (DtmUsedInDescriptorModel) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getId(), that.getId()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getId(), getVersion());
    }
}
