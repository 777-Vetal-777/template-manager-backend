package com.itextpdf.dito.manager.entity.template;

import com.itextpdf.dito.manager.entity.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "template_log")
public class TemplateLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "template_log_gen")
    @SequenceGenerator(name = "template_log_gen", sequenceName = "template_log_sequence", allocationSize = 1)
    private Long id;
    @Column(insertable = false)
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " author_id")
    private UserEntity author;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " template_id")
    private TemplateEntity template;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "TemplateLogEntity{" +
                "id=" + id +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                '}';
    }
}
