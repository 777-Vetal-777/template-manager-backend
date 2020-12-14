package com.itextpdf.dito.manager.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "template")
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_gen")
    @SequenceGenerator(name = "template_gen", sequenceName = "template_sequence", allocationSize = 1)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private TemplateTypeEntity type;

    @OneToOne(mappedBy = "template", fetch = FetchType.LAZY)
    private DataCollectionEntity dataCollection;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", referencedColumnName = "id")
    private InstanceEntity instance;

    @OneToMany(
            mappedBy = "template",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("version DESC")
    private List<TemplateFileEntity> files;

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

    public TemplateTypeEntity getType() {
        return type;
    }

    public void setType(TemplateTypeEntity type) {
        this.type = type;
    }

    public List<TemplateFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<TemplateFileEntity> files) {
        this.files = files;
    }

    public DataCollectionEntity getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionEntity dataCollection) {
        this.dataCollection = dataCollection;
    }

    public InstanceEntity getInstance() {
        return instance;
    }

    public void setInstance(InstanceEntity instance) {
        this.instance = instance;
    }
}
