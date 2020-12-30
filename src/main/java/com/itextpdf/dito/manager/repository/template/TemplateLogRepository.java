package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.template.TemplateLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateLogRepository extends JpaRepository<TemplateLogEntity, Long> {
}
