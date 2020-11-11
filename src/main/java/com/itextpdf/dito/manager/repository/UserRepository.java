package com.itextpdf.dito.manager.repository;

import com.itextpdf.dito.manager.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByActiveTrue();

    List<User> findAllByActiveTrue(Sort sort);

    Optional<User> findByIdAndActiveTrue(Long id);

    Optional<User> findByEmailAndActiveTrue(String email);
}
