package com.itextpdf.dito.manager.entity.resource;

import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resource_file")
public class ResourceFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "resource_file_gen")
    @SequenceGenerator(name = "resource_file_gen", sequenceName = "resource_file_sequence", allocationSize = 1)
    private Long id;
    private Long version;
    private byte[] file;
    private String fileName;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private ResourceEntity resource;
    private Boolean deployed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
    private Date createdOn;
    private Date modifiedOn;

    @ManyToMany(mappedBy = "resourceFiles")
    private Set<TemplateFileEntity> templateFiles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("( select stage.id from {h-schema}stage stage where stage.sequence_order = (select max(instanceStage.sequence_order) " +
            "from {h-schema}resource_file_template_file toTemplateFile " +
            "join {h-schema}template_file templateFile on toTemplateFile.template_file_id = templateFile.id " +
            "join {h-schema}template_file_instance toInstance on toInstance.template_file_id = templateFile.id " +
            "join {h-schema}instance instance on instance.id = toInstance.instance_id " +
            "join {h-schema}stage instanceStage on instanceStage.id = instance.stage_id " +
            "where toTemplateFile.resource_file_id = id) )")
    private StageEntity stage;

    public Set<TemplateFileEntity> getTemplateFiles() {
        return templateFiles;
    }

    public void setTemplateFiles(Set<TemplateFileEntity> templateFiles) {
        this.templateFiles = templateFiles;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Boolean getDeployed() {
        return deployed;
    }

    public void setDeployed(Boolean deployed) {
        this.deployed = deployed;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String coment) {
        this.comment = coment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }

    public StageEntity getStage() {
        return stage;
    }

    public void setStage(StageEntity stage) {
        this.stage = stage;
    }
}