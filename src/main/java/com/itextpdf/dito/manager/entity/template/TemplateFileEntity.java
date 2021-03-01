package com.itextpdf.dito.manager.entity.template;

import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "template_file")
public class TemplateFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_file_gen")
    @SequenceGenerator(name = "template_file_gen", sequenceName = "template_file_sequence", allocationSize = 1)
    private Long id;
    private byte[] data;
    private String comment;
    private Long version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " template_id")
    private TemplateEntity template;
    private Date createdOn;
    private Date modifiedOn;
    private Boolean deployed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_collection_file_id")
    private DataCollectionFileEntity dataCollectionFile;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "resource_file_template_file",
            joinColumns = @JoinColumn(name = "template_file_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_file_id"))
    private Set<ResourceFileEntity> resourceFiles = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "template_file_instance",
            joinColumns = @JoinColumn(name = "template_file_id"),
            inverseJoinColumns = @JoinColumn(name = "instance_id"))
    private Set<InstanceEntity> instance = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("( select stage.id from {h-schema}stage stage where stage.sequence_order = (select max(instanceStage.sequence_order) " +
            "from {h-schema}template_file_instance toInstance " +
            "join {h-schema}instance instance on instance.id = toInstance.instance_id " +
            "join {h-schema}stage instanceStage on instanceStage.id = instance.stage_id " +
            "where toInstance.template_file_id = id) )")
    private StageEntity stage;

    @OneToMany(
            mappedBy = "composition",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TemplateFilePartEntity> parts = new ArrayList<>();

    @OneToMany(
            mappedBy = "part",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<TemplateFilePartEntity> compositions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public DataCollectionFileEntity getDataCollectionFile() {
        return dataCollectionFile;
    }

    public void setDataCollectionFile(
            DataCollectionFileEntity dataCollectionFile) {
        this.dataCollectionFile = dataCollectionFile;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public void setTemplate(TemplateEntity template) {
        this.template = template;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Set<ResourceFileEntity> getResourceFiles() {
        return resourceFiles;
    }

    public void setResourceFiles(Set<ResourceFileEntity> resourceFiles) {
        this.resourceFiles = resourceFiles;
    }

    public Set<InstanceEntity> getInstance() {
        return instance;
    }

    public void setInstance(Set<InstanceEntity> instance) {
        this.instance = instance;
    }

    public Boolean getDeployed() {
        return deployed;
    }

    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
    }

    public StageEntity getStage() {
        return stage;
    }

    public void setStage(StageEntity stage) {
        this.stage = stage;
        this.instance = new HashSet<>(stage.getInstances());
    }

    public List<TemplateFilePartEntity> getParts() {
        return parts;
    }

    public List<TemplateFilePartEntity> getCompositions() {
        return compositions;
    }

    @Override
    public String toString() {
        return "TemplateFileEntity{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", version=" + version +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", deployed=" + deployed +
                '}';
    }
}
