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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "failed_login_attempt")
public class FailedLoginAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "failed_login_attempt_gen")
    @SequenceGenerator(name = "failed_login_attempt_gen", sequenceName = "failed_login_attempt_sequence", allocationSize = 1)
    private Long id;
    @Column(insertable = false)
    private Date version;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = " user_id")
    private UserEntity user;

    public FailedLoginAttemptEntity() {

    }

    public FailedLoginAttemptEntity(final UserEntity user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getVersion() {
        return version;
    }

    public void setVersion(Date version) {
        this.version = version;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "FailedLoginAttemptEntity{" +
                "id=" + id +
                ", version=" + version +
                '}';
    }
}
