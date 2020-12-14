package com.itextpdf.dito.manager.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;

@Entity(name = "instance")
public class InstanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "instance_gen")
    @SequenceGenerator(name = "instance_gen", sequenceName = "instance_sequence", allocationSize = 1)
    private Long id;
    private String name;
    private String socket;
    @Column(name = "created_on")
    private Date createdOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private UserEntity createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", referencedColumnName = "id")
    private StageEntity stage;
    @OneToOne(mappedBy = "instance")
    private TemplateEntity template;

    @PrePersist
    public void onPrePersist() {
        createdOn = new Date();
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

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public StageEntity getStage() {
        return stage;
    }

    public void setStage(StageEntity stage) {
        this.stage = stage;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public void setTemplate(TemplateEntity template) {
        this.template = template;
    }
}
