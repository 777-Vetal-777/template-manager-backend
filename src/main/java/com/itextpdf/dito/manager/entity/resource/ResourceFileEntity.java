package com.itextpdf.dito.manager.entity.resource;

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
@Table(name = "resource_file")
public class ResourceFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "resource_file_gen")
    @SequenceGenerator(name = "resource_file_gen", sequenceName = "resource_file_sequence", allocationSize = 1)
    private Long id;
    private Long version;
    private byte[] file;
    private String fileName;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " resource_id")
    private ResourceEntity resource;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String coment) {
        this.comment = coment;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ResourceEntity getResource() {
        return resource;
    }

    public void setResource(ResourceEntity resource) {
        this.resource = resource;
    }
}