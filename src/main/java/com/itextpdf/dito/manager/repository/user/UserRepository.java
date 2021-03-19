package com.itextpdf.dito.manager.repository.user;

import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "email", "firstName", "lastName", "active", "locked", "roles");

    @Query(value = "select distinct u from UserEntity u "
            + "join u.roles role")
    Page<UserEntity> findAll(Pageable pageable);

    @Query(value = "select user from UserEntity user "
            + "join user.roles role "
            + "where "
            //filtering
            + "("
            + "(:email='' or LOWER(user.email) like CONCAT('%',:email,'%')) "
            + "and (:firstName='' or LOWER(user.firstName) like CONCAT('%',:firstName,'%')) "
            + "and (:lastName='' or LOWER(user.lastName) like CONCAT('%',:lastName,'%')) "
            + "and (:active=null or user.active IS :active) "
            + "and (COALESCE(:securityRoles) is null or role.name in (:securityRoles))) "
            //search
            + "and (LOWER(user.email) like CONCAT('%',:search,'%') "
            + "or LOWER(role.name) like CONCAT('%',:search,'%') "
            + "or (LOWER(role.name) = 'global_administrator' and 'global administrator' like CONCAT('%',:search,'%')) "
            + "or (LOWER(role.name) = 'template_designer' and 'template designer' like CONCAT('%',:search,'%')) "
            + "or LOWER(CONCAT(user.firstName, ' ',  user.lastName)) like CONCAT('%',:search,'%'))"
            + "group by user.id")
    Page<UserEntity> search(Pageable pageable,
                            @Param("email") @Nullable String email,
                            @Param("firstName") @Nullable String firstName,
                            @Param("lastName") @Nullable String lastName,
                            @Param("securityRoles") @Nullable List<String> securityRoles,
                            @Param("active") @Nullable Boolean active,
                            @Param("search") String searchParam);

    @Query(value = "select user from UserEntity user "
            + "join user.roles role "
            + "where (:email='' or LOWER(user.email) like CONCAT('%',:email,'%')) "
            + "and (:firstName='' or LOWER(user.firstName) like CONCAT('%',:firstName,'%')) "
            + "and (:lastName='' or LOWER(user.lastName) like CONCAT('%',:lastName,'%')) "
            + "and (:active=null or user.active IS :active) "
            + "and (COALESCE(:securityRoles) is null or role.name in (:securityRoles)) "
            + "group by user.id")
    Page<UserEntity> filter(Pageable pageable,
                            @Param("email") @Nullable String email,
                            @Param("firstName") @Nullable String firstName,
                            @Param("lastName") @Nullable String lastName,
                            @Param("securityRoles") @Nullable List<String> securityRoles,
                            @Param("active") @Nullable Boolean active);

    @Query(value = "select count(u) from UserEntity u "
            + "join u.roles r "
            + "where u.roles.size = 1 "
            + "and r.name = :roleName ")
    int countOfUserWithOnlyOneRole(String roleName);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByLockedIsTrue();

    @Modifying
    @Query("update UserEntity u set u.active = :value where u.email in :emails")
    void updateActivationStatus(@Param("emails") List<String> emails, @Param("value") boolean active);

    Integer countDistinctByEmailIn(List<String> emails);
}
