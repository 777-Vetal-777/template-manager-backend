package com.itextpdf.dito.manager.entity.datacollection;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.JoinFormula;

@Entity
@Table(name = "data_collection")
public class DataCollectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "data_collection_gen")
    @SequenceGenerator(name = "data_collection_gen", sequenceName = "data_collection_sequence", allocationSize = 1)
    private Long id;

    private String name;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT file.id " +
            "FROM manager.data_collection_file file " +
            "WHERE file.data_collection_id = id and file.version=(" +
            "select max(file.version) from manager.data_collection_file file where file.data_collection_id = id))")
    private DataCollectionFileEntity latestVersion;

    @OneToMany(
            mappedBy = "dataCollection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<DataCollectionLogEntity> dataCollectionLog;

    @OneToMany(mappedBy = "dataCollection",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("version DESC")
    private Collection<DataCollectionFileEntity> versions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT max(log.id) " +
            "FROM {h-schema}data_collection_log log " +
            "WHERE log.data_collection_id = id and log.date=(" +
            "select max(logLatest.date) from {h-schema}data_collection_log logLatest where logLatest.data_collection_id = id)"
            +
            ")")
    private DataCollectionLogEntity lastDataCollectionLog;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "data_collection_role",
            joinColumns = @JoinColumn(
                    name = "data_collection_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"))
    private Set<RoleEntity> appliedRoles = new HashSet<>();

    @OneToMany(mappedBy = "dataCollection")
    private Collection<DataSampleEntity> dataSamples;

    public Collection<DataCollectionLogEntity> getDataCollectionLog() {
        return dataCollectionLog;
    }

    public void setDataCollectionLog(Collection<DataCollectionLogEntity> dataCollectionLog) {
        this.dataCollectionLog = dataCollectionLog;
    }

    public DataCollectionFileEntity getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(DataCollectionFileEntity latestVersion) {
        this.latestVersion = latestVersion;
    }

    public DataCollectionLogEntity getLastDataCollectionLog() {
        return lastDataCollectionLog;
    }

    public void setLastDataCollectionLog(DataCollectionLogEntity lastDataCollectionLog) {
        this.lastDataCollectionLog = lastDataCollectionLog;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return getLastDataCollectionLog().getAuthor();
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

    public Collection<DataCollectionFileEntity> getVersions() {
        return versions;
    }

    public void setVersions(Collection<DataCollectionFileEntity> versions) {
        this.versions = versions;
    }

    public Set<RoleEntity> getAppliedRoles() {
        return appliedRoles;
    }

    public void setAppliedRoles(Set<RoleEntity> appliedRoles) {
        this.appliedRoles = appliedRoles;
    }

    public Collection<DataSampleEntity> getDataSamples() {
        return dataSamples;
    }

    public void setDataSamples(
            Collection<DataSampleEntity> dataSamples) {
        this.dataSamples = dataSamples;
    }
}