package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type", "dataCollection", "modifiedOn", "modifiedOn");

    Optional<TemplateEntity> findByName(String name);

    @Query(value = "select template from TemplateEntity template "
            + "join template.type type "
            + "join template.files file "
            + "left join template.dataCollection "
            + "where "
            //filtering
            + "(:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or LOWER(template.type.name) in (:types)) "
            + "and (:modifiedBy='' or LOWER(file.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(file.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (COALESCE(:modificationPeriod) is null or file.version in (:modificationPeriod)) "
            + "and (template.dataCollection is null or (:dataCollectionName='' or LOWER(template.dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))"
            + "group by template.id")
    Page<TemplateEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<String> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("modificationPeriod") @Nullable List<Date> modificationPeriod);


    @Query(value = "select template from TemplateEntity template "
            + "join template.type type "
            + "join template.files file "
            + "left join template.dataCollection "
            + "where "
            //filtering
            + "((:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or LOWER(template.type.name) in (:types)) "
            + "and (:modifiedBy='' or LOWER(file.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(file.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (COALESCE(:modificationPeriod) is null or file.version in (:modificationPeriod)) "
            + "and (template.dataCollection is null or (:dataCollectionName='' or LOWER(template.dataCollection.name) like CONCAT('%',:dataCollectionName,'%'))))"
            //search
            + "and (LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or LOWER(template.dataCollection.name) like CONCAT('%',:search,'%') "
            + "or LOWER(template.type.name) like CONCAT('%',:search,'%') "
            + "or LOWER(file.author.firstName) like CONCAT('%',:search,'%') "
            + "or LOWER(file.author.lastName) like CONCAT('%',:search,'%')) "
            + "group by template.id")
    Page<TemplateEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<String> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("modificationPeriod") @Nullable List<Date> modificationPeriod,
                                @Param("search") String searchParam);

    @Override
    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "join template.type type ")
    Page<TemplateEntity> findAll(Pageable pageable);
}
