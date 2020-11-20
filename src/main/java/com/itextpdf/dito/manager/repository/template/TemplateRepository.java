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
    Optional<TemplateEntity> findByName(String name);

    @Query(value = "select t from TemplateEntity t "
            + "join t.files file "
            + "where t.name like '%'||:value||'%' "
            + "or t.type.name like '%'||:value||'%' "
            + "or file.author.email like '%'||:value||'%' ")
    Page<TemplateEntity> search(Pageable pageable, @Param("value") String searchParam);

    @Override
    @Query(value = "select t from TemplateEntity t "
            + "join t.files file")
    Page<TemplateEntity> findAll(Pageable pageable);
}
