package com.itextpdf.dito.manager.entity;

import java.util.Date;
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
@Table(name = "template_file")
public class TemplateFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_file_gen")
    @SequenceGenerator(name = "template_file_gen", sequenceName = "template_file_sequence", allocationSize = 1)
    private Long id;
    private byte[] data;
    private String comment;
    private Date version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " template_id")
    private TemplateEntity template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public TemplateEntity getTemplate() {
        return template;
    }

    public void setTemplate(TemplateEntity template) {
        this.template = template;
    }
}
