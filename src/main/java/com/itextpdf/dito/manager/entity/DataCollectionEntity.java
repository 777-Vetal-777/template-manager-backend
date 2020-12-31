package com.itextpdf.dito.manager.entity;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    @Column
    private Date createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id", referencedColumnName = "id")
    private UserEntity author;

    @OneToMany(mappedBy = "dataCollection")
    private List<TemplateEntity> templates;

    @OneToMany(
            mappedBy = "dataCollection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<DataCollectionLogEntity> dataCollectionLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT log.id " +
            "FROM manager.data_collection_log log " +
            "WHERE log.data_collection_id = id and log.date=(" +
            "select max(logLatest.date) from manager.data_collection_log logLatest where logLatest.data_collection_id = id)" +
            ")")
    private DataCollectionLogEntity lastDataCollectionLog;

    public Collection<DataCollectionLogEntity> getDataCollectionLog() {
        return dataCollectionLog;
    }

    public void setDataCollectionLog(Collection<DataCollectionLogEntity> dataCollectionLog) {
        this.dataCollectionLog = dataCollectionLog;
    }

    public DataCollectionLogEntity getLatestLogRecord() {
        return lastDataCollectionLog;
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

    public UserEntity getModifiedBy() {
        return getLatestLogRecord().getAuthor();
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public List<TemplateEntity> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplateEntity> templates) {
        this.templates = templates;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] file) {
        this.data = file;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}