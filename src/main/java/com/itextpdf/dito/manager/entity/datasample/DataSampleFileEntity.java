package com.itextpdf.dito.manager.entity.datasample;

import com.itextpdf.dito.manager.entity.UserEntity;

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
@Table(name = "data_sample_file")
public class DataSampleFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_sample_file_gen")
    @SequenceGenerator(name = "data_sample_file_gen", sequenceName = "data_sample_file_sequence", allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_sample_id")
    private DataSampleEntity dataSample;
    private String comment;
    private Long version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
    private Date createdOn;
    private byte[] data;
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataSampleEntity getDataSample() {
        return dataSample;
    }

    public void setDataSample(DataSampleEntity dataSample) {
        this.dataSample = dataSample;
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

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
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

    @Override
    public String toString() {
        return "DataSampleFileEntity{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", version=" + version +
                ", createdOn=" + createdOn +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
