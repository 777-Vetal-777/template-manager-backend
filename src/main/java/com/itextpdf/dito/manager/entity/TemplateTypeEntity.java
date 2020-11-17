package com.itextpdf.dito.manager.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "template_type")
public class TemplateTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_type_gen")
    @SequenceGenerator(name = "template_type_gen", sequenceName = "template_type_sequence", allocationSize = 1)
    private Long id;
    private String name;

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
}
