package com.itextpdf.dito.manager.entity;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "data_collection")
public class DataCollectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_collection_gen")
    @SequenceGenerator(name = "data_collection_gen", sequenceName = "data_collection_sequence", allocationSize = 1)
    private Long id;

    private String name;

    private byte[] data;

    private String fileName;

    private String description;

    @Enumerated(EnumType.STRING)
    private DataCollectionType type;
    @Column(insertable = false)
    private Date modifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id", referencedColumnName = "id")
    private UserEntity author;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private TemplateEntity template;

    @OneToMany(
            mappedBy = "dataCollection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<DataCollectionLogEntity> dataCollectionLog;

    public Collection<DataCollectionLogEntity> getDataCollectionLog() {
        return dataCollectionLog;
    }

    public void setDataCollectionLog(Collection<DataCollectionLogEntity> dataCollectionLog) {
        this.dataCollectionLog = dataCollectionLog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    public DataCollectionType getType() {
        return type;
    }

    public void setType(DataCollectionType type) {
        this.type = type;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] file) {
        this.data = file;
    }

}