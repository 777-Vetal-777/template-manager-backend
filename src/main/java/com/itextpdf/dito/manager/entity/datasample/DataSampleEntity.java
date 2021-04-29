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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "data_sample")
public class DataSampleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_sample_gen")
    @SequenceGenerator(name = "data_sample_gen", sequenceName = "data_sample_sequence", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_collection_id")
    private DataCollectionEntity dataCollection;  
    private String name; 
    private String description;   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
    @Column(name="modified_on")
    private Date modifiedOn;
    @Column(name="created_on")
    private Date createdOn;
    @Column(name="is_default")
    private Boolean isDefault;
    @Column(name = "uuid")
    private String uuid;

    @OneToMany(mappedBy = "dataSample",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("version DESC")
    private Collection<DataSampleFileEntity> versions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT max(log.id) " +
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

    @PrePersist
    public void onPrePersist() {
        uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

    public Date getModifiedOn() {
        return modifiedOn;
    }

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataCollectionEntity getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionEntity dataCollection) {
        this.dataCollection = dataCollection;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
        return getIsDefault();
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
    
    public UserEntity getModifiedBy() {
        return getLastDataSampleLog().getAuthor();
    }

    @Override
    public String toString() {
        return "DataSampleEntity{" +
                "Id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", modifiedOn=" + modifiedOn +
                ", createdOn=" + createdOn +
                ", isDefault=" + isDefault +
                '}';
    }
}
