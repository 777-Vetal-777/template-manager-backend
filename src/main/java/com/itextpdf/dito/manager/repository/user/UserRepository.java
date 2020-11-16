package com.itextpdf.dito.manager.repository.user;

import com.itextpdf.dito.manager.entity.UserEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByActiveTrue();

    List<UserEntity> findAllByActiveTrue(Sort sort);

    Optional<UserEntity> findByIdAndActiveTrue(Long id);

    Optional<UserEntity> findByEmailAndActiveTrue(String email);
}
