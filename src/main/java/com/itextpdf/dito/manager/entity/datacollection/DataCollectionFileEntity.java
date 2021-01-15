package com.itextpdf.dito.manager.entity.datacollection;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "data_collection_file")
public class DataCollectionFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_collection_file_gen")
    @SequenceGenerator(name = "data_collection_file_gen", sequenceName = "data_collection_file_sequence", allocationSize = 1)
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_collection_id")
    private DataCollectionEntity dataCollection;
    private Long version;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
    private Date createdOn;
    private byte[] data;
    private String fileName;
    @OneToMany(mappedBy = "dataCollectionFile")
    private List<TemplateFileEntity> templateFiles;

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public DataCollectionEntity getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionEntity dataCollection) {
        this.dataCollection = dataCollection;
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

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
