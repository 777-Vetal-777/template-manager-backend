package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.entity.resource.ResourceLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceLogRepository extends JpaRepository<ResourceLogEntity, Long> {
    ResourceLogEntity findFirstByResource_IdOrderByDateDesc(Long resourceId);
}
