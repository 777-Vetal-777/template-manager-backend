package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFileEntity, Long> {
        ResourceFileEntity findFirstByResource_IdOrderByVersionDesc(Long id);
}
