package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type", "dataCollection", "modifiedBy", "editedOn");

    Optional<TemplateEntity> findByName(String name);

    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "left join template.dataCollectionFile dataCollectionFile "
            + "left join dataCollectionFile.dataCollection dataCollection "
            + "where "
            //filtering
            + "(:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or template.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date)) "
            + "and ((:dataCollectionName <> '' and (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))"
            + "or (:dataCollectionName = '' and (dataCollection.name is null or  (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))))")
    Page<TemplateEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<TemplateTypeEnum> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);


    @Query(value = "select template from TemplateEntity template "
            + "join template.files file "
            + "left join template.dataCollectionFile dataCollectionFile "
            + "left join dataCollectionFile.dataCollection dataCollection "
            + "where "
            //filtering
            + "((:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or template.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date)) "
            + "and ((:dataCollectionName <> '' and (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))"
            + "or (:dataCollectionName = '' and (dataCollection.name is null or  (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%'))))))"
            //search
            + "and (LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or LOWER(dataCollection.name) like CONCAT('%',:search,'%') "
            + "or LOWER(template.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(template.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%') ")
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

    @Query(value = "select template from TemplateEntity template "
            + "left join template.resources versions "
            + "left join versions.resource resource "
            + "where resource.id = :resourceId "
            + "group by template.id")
    List<TemplateEntity> findTemplatesByResourceId(@Param("resourceId") Long resourceId);

    @Query(value = "select template from TemplateEntity template "
            + "left join template.dataCollectionFile version "
            + "left join version.dataCollection datacollection "
            + "where datacollection.id = :dataCollectionId "
            + "group by template.id")
    List<TemplateEntity> findTemplatesByDataCollectionId(@Param("dataCollectionId") Long dataCollectionId);
}
