package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type", "dataCollection", "modifiedBy", "editedOn");

    Optional<TemplateEntity> findByName(String name);

    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "left join template.dataCollection dataCollection "
            + "where "
            //filtering
            + "(:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(file.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(file.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or file.version between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (dataCollection is null or (:dataCollectionName='' or LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))")
    Page<TemplateEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<TemplateTypeEnum> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);


    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "left join template.dataCollection dataCollection "
            + "where "
            //filtering
            + "((:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(file.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(file.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or file.version between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (dataCollection is not null and (:dataCollectionName='' or LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%'))))"
            //search
            + "and (LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or LOWER(dataCollection.name) like CONCAT('%',:search,'%') "
            + "or LOWER(template.type) like CONCAT('%',:search,'%') "
            + "or LOWER(file.author.firstName) like CONCAT('%',:search,'%') "
            + "or LOWER(file.author.lastName) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(file.version as date) as string) like CONCAT('%',:search,'%') ")
    Page<TemplateEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<TemplateTypeEnum> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("startDate") @Nullable @Temporal Date startDate,
                                @Param("endDate") @Nullable @Temporal Date endDate,
                                @Param("search") String searchParam);

    @Override
    @Query(value = "select template from TemplateEntity template "
            + "join template.files file ")
    Page<TemplateEntity> findAll(Pageable pageable);
}
