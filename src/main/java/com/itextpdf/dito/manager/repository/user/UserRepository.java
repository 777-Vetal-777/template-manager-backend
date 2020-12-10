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
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "email", "firstName", "lastName", "active", "locked", "role.name");

    @Query(value = "select distinct u from UserEntity u "
            + "join u.roles role")
    Page<UserEntity> findAll(Pageable pageable);

    @Query(value = "select u from UserEntity u "
            + "join u.roles role  "
            + "where LOWER(u.email) like LOWER(CONCAT('%',:value,'%')) "
            + "or LOWER(role.name) like LOWER(CONCAT('%',:value,'%')) "
            + "or LOWER(u.firstName) like LOWER(CONCAT('%',:value,'%')) "
            + "or LOWER(u.lastName) like LOWER(CONCAT('%',:value,'%'))")
    Page<UserEntity> search(Pageable pageable, @Param("value") String searchParam);

    @Query(value = "select count(u) from UserEntity u "
            + "join u.roles r "
            + "where u.roles.size = 1 "
            + "and r.name = :roleName ")
    Integer countOfUserWithOnlyOneRole(String roleName);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByLockedIsTrue();

    @Modifying
    @Query("update UserEntity u set u.active = :value where u.email in :emails")
    void updateActivationStatus(@Param("emails") List<String> emails, @Param("value") boolean active);

    Integer countDistinctByEmailIn(List<String> emails);
}
