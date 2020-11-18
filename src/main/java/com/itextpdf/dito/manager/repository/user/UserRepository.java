package com.itextpdf.dito.manager.repository.user;

import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Page<UserEntity> findAllByActiveTrue(Pageable pageable);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);

    Optional<UserEntity> findByEmail(String email);
}
