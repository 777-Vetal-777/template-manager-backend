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
    Page<UserEntity> findAll(Pageable pageable);

    @Query(value = "select u from UserEntity u "
            + "where u.email like '%'||:value||'%' "
            + "or u.firstName like '%'||:value||'%' "
            + "or u.lastName like '%'||:value||'%'")
    Page<UserEntity> search(Pageable pageable, @Param("value") String searchParam);

    @Query(value = "select count(r) from UserEntity u "
            + "join u.roles r "
            + "where r.name like :roleName "
            + "and count(r)<2 ")
    Integer countOfUserWithOnlyOneRole(String roleName);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findAllByLockedIsTrue();

    @Modifying
    @Query("update UserEntity u set u.active = :value where u.email in :emails")
    void activateUsers(@Param("emails") List<String> emails, @Param("value") boolean active);

    Integer countDistinctByEmailIn(List<String> emails);
}
