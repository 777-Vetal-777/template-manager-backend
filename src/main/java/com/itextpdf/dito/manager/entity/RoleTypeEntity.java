package com.itextpdf.dito.manager.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "role_type")
public class RoleTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "role_type_gen")
    @SequenceGenerator(name = "role_type_gen", sequenceName = "role_type_sequence", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleType name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }
}
