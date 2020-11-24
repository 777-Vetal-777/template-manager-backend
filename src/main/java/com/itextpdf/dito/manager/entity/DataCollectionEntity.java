package com.itextpdf.dito.manager.entity;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "data_collection")
public class DataCollectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "datacollection_gen")
    @SequenceGenerator(name = "datacollection_gen", sequenceName = "datacollection_sequence", allocationSize = 1)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private DataCollectionType type;

    @Column(insertable = false)
    private Date modifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;

    public DataCollectionEntity() {
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
}