package com.itextpdf.dito.manager.entity.datacollection;

import com.itextpdf.dito.manager.entity.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "data_collection_file")
public class DataCollectionFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "dataCollectionFile_gen")
    @SequenceGenerator(name = "dataCollectionFile_gen", sequenceName = "data_collection_file_sequence", allocationSize = 1)
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " template_id")
    private DataCollectionEntity dataCollection;
    private Long version;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;
    private Date createdOn;

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
}
