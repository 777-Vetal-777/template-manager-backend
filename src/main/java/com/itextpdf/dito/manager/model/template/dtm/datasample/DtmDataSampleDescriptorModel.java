package com.itextpdf.dito.manager.model.template.dtm.datasample;
import java.util.List;

public class DtmDataSampleDescriptorModel {
    private Long id;
    private String name;
    private String description;
    private List<DtmDataSampleVersionDescriptorModel> versions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DtmDataSampleVersionDescriptorModel> getVersions() {
        return versions;
    }

    public void setVersions(List<DtmDataSampleVersionDescriptorModel> versions) {
        this.versions = versions;
    }
}
