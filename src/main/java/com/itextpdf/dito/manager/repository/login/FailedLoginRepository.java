package com.itextpdf.dito.manager.repository.login;

import com.itextpdf.dito.manager.entity.FailedLoginAttemptEntity;
import com.itextpdf.dito.manager.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedLoginRepository extends JpaRepository<FailedLoginAttemptEntity, Long> {
    Optional<List<FailedLoginAttemptEntity>> findByUser(UserEntity user);

    void deleteByUser(UserEntity user);

    int countByUser(UserEntity user);
}
