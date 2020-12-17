package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
    Boolean existsByNameEqualsAndTypeEquals(String resourceName, ResourceTypeEnum type);

    Optional<ResourceEntity> findByNameAndType(String resourcesName, ResourceTypeEnum type);

    ResourceEntity findByName(String name);

    Page<ResourceEntity> findAll(Pageable pageable);
}
