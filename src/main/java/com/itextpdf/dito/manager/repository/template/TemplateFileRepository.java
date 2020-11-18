package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateFileEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateFileRepository extends JpaRepository<TemplateFileEntity, Long> {
}
