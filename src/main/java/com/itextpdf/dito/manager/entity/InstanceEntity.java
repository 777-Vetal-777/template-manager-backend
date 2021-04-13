package com.itextpdf.dito.manager.entity;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "stage_id", referencedColumnName = "id")
    private StageEntity stage;
    @ManyToMany(mappedBy = "instance")
    private List<TemplateFileEntity> templateFile;
    @Column(name = "register_token")
    private String registerToken;
    @Column(name = "header_name")
    private String headerName;
    @Column(name = "header_value")
    private String headerValue;
    @Column(insertable = false)
    private Boolean active;

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

    public List<TemplateFileEntity> getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(List<TemplateFileEntity> templateFile) {
        this.templateFile = templateFile;
    }

    public String getRegisterToken() {
        return registerToken;
    }

    public void setRegisterToken(String registerToken) {
        this.registerToken = registerToken;
    }

    public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

	@Override
    public String toString() {
        return "InstanceEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", socket='" + socket + '\'' +
                ", createdOn=" + createdOn +
                ", headerName='" + headerName + '\'' +
                ", headerValue='" + headerValue + '\'' +
                ", registerToken='" + registerToken + '\'' +
                ", active='" + active + '\'' +
                '}';
    }
}
