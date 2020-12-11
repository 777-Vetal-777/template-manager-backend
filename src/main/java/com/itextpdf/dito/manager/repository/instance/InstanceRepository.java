package com.itextpdf.dito.manager.repository.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {
    Optional<InstanceEntity> findByName(String name);

    Page<InstanceEntity> findAll(Specification<InstanceEntity> specification, Pageable pageable);
}
