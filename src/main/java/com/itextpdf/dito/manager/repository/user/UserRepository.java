package com.itextpdf.dito.manager.repository.user;

import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByActiveTrue(Pageable pageable);

    @Query(value = "select u from UserEntity u "
            + "where (u.email like '%'||:value||'%' "
            + "or u.firstName like '%'||:value||'%' "
            + "or u.lastName like '%'||:value||'%') "
            + "and u.active=true")
    Page<UserEntity> search(Pageable pageable, @Param("value") String searchParam);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByEmail(String email);
}
