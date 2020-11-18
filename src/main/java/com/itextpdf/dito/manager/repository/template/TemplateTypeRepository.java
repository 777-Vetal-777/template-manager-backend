package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEntity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateTypeRepository extends JpaRepository<TemplateTypeEntity, Long> {
    Optional<TemplateTypeEntity> findByName(String name);
}
