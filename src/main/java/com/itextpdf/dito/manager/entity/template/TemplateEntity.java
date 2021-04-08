package com.itextpdf.dito.manager.entity.template;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import org.hibernate.annotations.JoinFormula;

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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private Date blockedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by")
    private UserEntity blockedBy;
    @Column(name = "uuid")
    private String uuid;

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
            "SELECT max(log.id) " +
            "FROM manager.template_log log " +
            "WHERE log.template_id = id and log.date=(" +
            "select max(log.date) from manager.template_log log where log.template_id = id)" +
            ")")
    private TemplateLogEntity latestLogRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT min(log.id) " +
            "FROM manager.template_log log " +
            "WHERE log.template_id = id and log.date=(" +
            "select min(log.date) from manager.template_log log where log.template_id = id)" +
            ")")
    private TemplateLogEntity firstLogRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("(" +
            "SELECT file.id " +
            "FROM manager.template_file file " +
            "WHERE file.template_id = id and file.version=(" +
            "select max(file.version) from manager.template_file file where file.template_id = id)" +
            ")")
    private TemplateFileEntity latestFile;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "template_role",
            joinColumns = @JoinColumn(
                    name = "template_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"))
    private Set<RoleEntity> appliedRoles = new HashSet<>();

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

    public Set<RoleEntity> getAppliedRoles() {
        return appliedRoles;
    }

    public void setAppliedRoles(Set<RoleEntity> appliedRoles) {
        this.appliedRoles = appliedRoles;
    }

    public Date getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(Date blockedAt) {
        this.blockedAt = blockedAt;
    }

    public UserEntity getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(UserEntity blockedBy) {
        this.blockedBy = blockedBy;
    }

    @Override
    public String toString() {
        return "TemplateEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", blockedAt=" + blockedAt +
                '}';
    }
}
