package com.itextpdf.dito.manager.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "workspace")
public class WorkspaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "workspace_gen")
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_sequence", allocationSize = 1)
    private Long id;
    private String name;
    private String language;
    private String timezone;
    @Column(name = "adjust_for_daylight", insertable = false)
    private Boolean adjustForDaylight;
    @OneToOne(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private PromotionPathEntity promotionPath;
    @OneToOne(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private LicenseEntity licenseEntity;
    
    public void setPromotionPath(PromotionPathEntity promotionPath) {
        if (this.promotionPath != null) {
            this.promotionPath.setWorkspace(null);
        }
        promotionPath.setWorkspace(this);
        this.promotionPath = promotionPath;
    }

    public Boolean getAdjustForDaylight() {
        return adjustForDaylight;
    }

    public void setAdjustForDaylight(Boolean adjustForDaylight) {
        this.adjustForDaylight = adjustForDaylight;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public PromotionPathEntity getPromotionPath() {
        return promotionPath;
    }

	public LicenseEntity getLicenseEntity() {
		return licenseEntity;
	}

	public void setLicenseEntity(LicenseEntity licenseEntity) {
		this.licenseEntity = licenseEntity;
	}

    @Override
    public String toString() {
        return "WorkspaceEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", timezone='" + timezone + '\'' +
                ", adjustForDaylight=" + adjustForDaylight +
                '}';
    }
}
