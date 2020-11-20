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

    @Query(value = "select * from manager.template t "
            + "join manager.template_file tf on tf.template_id=t.id "
            + "join manager.template_type tt on t.type_id=tt.id "
            + "join manager.user u on tf.author_id=u.id "
            + "where t.name like '%'||:value||'%' "
            + "or t.name like '%'||:value||'%' "
            + "or tt.name like '%'||:value||'%' "
            + "or u.email like '%'||:value||'%'",
    nativeQuery = true)
    Page<TemplateEntity> search(Pageable pageable, @Param("value") String searchParam);
}
