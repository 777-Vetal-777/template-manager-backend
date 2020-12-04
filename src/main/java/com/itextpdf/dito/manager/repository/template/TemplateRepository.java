package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type.name", "file.author.email", "file.version", "type.name", "dataCollection.name");

    Optional<TemplateEntity> findByName(String name);

    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "join template.type type "
            + "where LOWER(template.name) like LOWER(CONCAT('%',:value,'%'))  "
            + "or  LOWER(type.name) like LOWER(CONCAT('%',:value,'%'))  "
            + "or  LOWER(file.author.email) like LOWER(CONCAT('%',:value,'%')) "
            + "or cast(file.version as text) like '%'||:value||'%'")
    Page<TemplateEntity> search(Pageable pageable, @Param("value") String searchParam);

    @Override
    @Query(value = "select template from TemplateEntity template "
            + "join template.files file ")
    Page<TemplateEntity> findAll(Pageable pageable);
}
