package com.itextpdf.dito.manager.entity.datasample;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "data_sample")
public class DataSampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_sample_gen")
    @SequenceGenerator(name = "data_sample_gen", sequenceName = "data_sample_sequence", allocationSize = 1)
    private Long Id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_collection_id")
    private DataCollectionEntity dataCollection;  
    private String name; 
    private String comment;   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
    @Column(name="modified_on")
    private Date modifiedOn;
    @Column(name="created_on")
    private Date createdOn;
    @Column(name="is_default")
    private Boolean isDefault;
    public Date getModifiedOn() {
		return modifiedOn;
	}

    @OneToMany(mappedBy = "dataSample",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("version DESC")
    private Collection<DataSampleFileEntity> versions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT log.id " +
            "FROM {h-schema}data_sample_log log " +
            "WHERE log.data_sample_id = id and log.date=(" +
            "select max(logLatest.date) from {h-schema}data_sample_log logLatest where logLatest.data_sample_id = id)" +
            ")")
    private DataSampleLogEntity lastDataSampleLog;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT file.id " +
            "FROM manager.data_sample_file file " +
            "WHERE file.data_sample_id = id and file.version=(" +
            "select max(file.version) from manager.data_sample_file file where file.data_sample_id = id))")
    private DataSampleFileEntity latestVersion;

    @OneToMany(
            mappedBy = "dataSample",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<DataSampleLogEntity> dataSampleLog;

    public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
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

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Collection<DataSampleFileEntity> getVersions() {
        return versions;
    }

    public void setVersions(Collection<DataSampleFileEntity> versions) {
        this.versions = versions;
    }

    public DataSampleLogEntity getLastDataSampleLog() {
        return lastDataSampleLog;
    }

    public void setLastDataSampleLog(DataSampleLogEntity lastDataSampleLog) {
        this.lastDataSampleLog = lastDataSampleLog;
    }

    public DataSampleFileEntity getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(DataSampleFileEntity latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Collection<DataSampleLogEntity> getDataSampleLog() {
        return dataSampleLog;
    }

    public void setDataSampleLog(Collection<DataSampleLogEntity> dataSampleLog) {
        this.dataSampleLog = dataSampleLog;
    }
}
