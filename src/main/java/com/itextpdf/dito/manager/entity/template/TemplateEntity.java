package com.itextpdf.dito.manager.entity.template;

import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.CascadeType;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "template")
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_gen")
    @SequenceGenerator(name = "template_gen", sequenceName = "template_sequence", allocationSize = 1)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TemplateTypeEnum type;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " data_collection_id")
    private DataCollectionEntity dataCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", referencedColumnName = "id")
    private InstanceEntity instance;

    @OneToMany(
            mappedBy = "template",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("version DESC")
    private List<TemplateFileEntity> files;

    @OneToMany(
            mappedBy = "template",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("date DESC")
    private Collection<TemplateLogEntity> templateLogs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT log.id " +
            "FROM manager.template_log log " +
            "WHERE log.template_id = id and log.date=(" +
            "select max(log.date) from manager.template_log log where log.template_id = id)" +
            ")")
    private TemplateLogEntity latestLogRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT file.id " +
            "FROM manager.template_file file " +
            "WHERE file.template_id = id and file.version=(" +
            "select max(file.version) from manager.template_file file where file.template_id = id)" +
            ")")
    private TemplateFileEntity latestFile;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "resource_file_template",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_file_id"))
    private Set<ResourceFileEntity> resources = new HashSet<>();

    public Set<ResourceFileEntity> getResources() {
        return resources;
    }

    public void setResources(Set<ResourceFileEntity> resources) {
        this.resources = resources;
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

    public TemplateTypeEnum getType() {
        return type;
    }

    public void setType(TemplateTypeEnum type) {
        this.type = type;
    }

    public List<TemplateFileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<TemplateFileEntity> files) {
        this.files = files;
    }

    public DataCollectionEntity getDataCollection() {
        return dataCollection;
    }

    public void setDataCollection(DataCollectionEntity dataCollection) {
        this.dataCollection = dataCollection;
    }

    public InstanceEntity getInstance() {
        return instance;
    }

    public void setInstance(InstanceEntity instance) {
        this.instance = instance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<TemplateLogEntity> getTemplateLogs() {
        return templateLogs;
    }

    public void setTemplateLogs(Collection<TemplateLogEntity> templateLogs) {
        this.templateLogs = templateLogs;
    }

    public TemplateLogEntity getLatestLogRecord() {
        return latestLogRecord;
    }

    public void setLatestLogRecord(TemplateLogEntity latestLogRecord) {
        this.latestLogRecord = latestLogRecord;
    }

    public TemplateFileEntity getLatestFile() {
        return latestFile;
    }

    public void setLatestFile(TemplateFileEntity latestFile) {
        this.latestFile = latestFile;
    }
}
