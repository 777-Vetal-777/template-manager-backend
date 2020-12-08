package com.itextpdf.dito.manager.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "data_collection_log")
public class DataCollectionLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_collection_log_gen")
    @SequenceGenerator(name = "data_collection_log_gen", sequenceName = "data_collection_sequence_log", allocationSize = 1)

    private Long id;
    @Column(insertable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " data_collection_id")
    private DataCollectionEntity dataCollection;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public DataCollectionEntity getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionEntity dataCollectionId) {
        this.dataCollection = dataCollectionId;
    }
}
