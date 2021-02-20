package com.itextpdf.dito.manager.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user")
public class UserEntity implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_sequence", allocationSize = 1)
    private Long id;
    private String email;
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(insertable = false)
    private Boolean active;
    @Column(insertable = false)
    private Boolean locked;
    @Column(name = "modified_at")
    private Date modifiedAt;
    @Column(name = "reset_password_token_date")
    private Date resetPasswordTokenDate;
    @Column(name = "password_updated_by_admin")
    private Boolean passwordUpdatedByAdmin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        final Set<SimpleGrantedAuthority> result = new HashSet<>();

        for (final RoleEntity roleEntity : roles) {
            final Set<PermissionEntity> permissions = roleEntity.getPermissions();
            for (final PermissionEntity permissionEntity : permissions) {
                result.add(new SimpleGrantedAuthority(permissionEntity.getName()));
            }
        }

        return result;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getLocked() == null ? Boolean.TRUE : !getLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getActive() == null ? Boolean.TRUE : getActive();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Boolean getPasswordUpdatedByAdmin() {
        return passwordUpdatedByAdmin;
    }

    public void setPasswordUpdatedByAdmin(Boolean passwordUpdated) {
        this.passwordUpdatedByAdmin = passwordUpdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getResetPasswordTokenDate() {
        return resetPasswordTokenDate;
    }

    public void setResetPasswordTokenDate(Date resetPasswordTokenDate) {
        this.resetPasswordTokenDate = resetPasswordTokenDate;
    }
}

