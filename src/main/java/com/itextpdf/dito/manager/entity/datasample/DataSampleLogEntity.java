package com.itextpdf.dito.manager.entity.datasample;

import com.itextpdf.dito.manager.entity.UserEntity;

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
@Table(name = "data_sample_log")
public class DataSampleLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_sample_log_gen")
    @SequenceGenerator(name = "data_sample_log_gen", sequenceName = "data_sample_log_sequence", allocationSize = 1)
    private Long id;

    @Column(insertable = false)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_sample_id")
    private DataSampleEntity dataSample;

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

    public DataSampleEntity getDataSample() {
        return dataSample;
    }

    public void setDataSample(DataSampleEntity dataSample) {
        this.dataSample = dataSample;
    }

    @Override
    public String toString() {
        return "DataSampleLogEntity{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}
