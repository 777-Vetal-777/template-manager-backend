package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    Optional<TemplateEntity> findByName(String name);
}
