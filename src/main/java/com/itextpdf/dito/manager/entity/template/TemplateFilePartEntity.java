package com.itextpdf.dito.manager.entity.template;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "template_file_part")
public class TemplateFilePartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_file_part_gen")
    @SequenceGenerator(name = "template_file_part_gen", sequenceName = "template_file_part_sequence", allocationSize = 1)
    private Long id;
    private String condition;
    private String settings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_file_id")
    private TemplateFileEntity composition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_file_part_id")
    private TemplateFileEntity part;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public TemplateFileEntity getComposition() {
        return composition;
    }

    public void setComposition(TemplateFileEntity composition) {
        this.composition = composition;
    }

    public TemplateFileEntity getPart() {
        return part;
    }

    public void setPart(TemplateFileEntity part) {
        this.part = part;
    }

    @Override
    public String toString() {
        return "TemplateFilePartEntity{" +
                "id=" + id +
                ", condition='" + condition + '\'' +
                '}';
    }
}
