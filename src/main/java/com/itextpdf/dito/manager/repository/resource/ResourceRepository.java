package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
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
public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "type", "modifiedBy", "modifiedOn", "comment");

    Boolean existsByNameEqualsAndTypeEquals(String resourceName, ResourceTypeEnum type);

    Optional<ResourceEntity> findByNameAndType(String resourcesName, ResourceTypeEnum type);

    ResourceEntity findByName(String name);

    @Query(value = "select resource from ResourceEntity resource "
            + "join resource.resourceFiles files "
            + "left join resource.resourceLogs logs "
            + "where "
            //filtering
            + "(:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(files.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(logs.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(logs.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or logs.date between cast(:startDate as date) and cast(:endDate as date)) "
            + "group by resource.id")
    Page<ResourceEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(value = "select resource from ResourceEntity resource "
            + "join resource.resourceFiles files "
            + "left join resource.resourceLogs logs "
            + "where "
            //filtering
            + "(:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(files.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(logs.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(logs.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or logs.date between cast(:startDate as date) and cast(:endDate as date)) "
            //search
            + "and (LOWER(resource.name) like CONCAT('%',:search,'%') "
            + "or LOWER(files.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(logs.author.firstName) like CONCAT('%',:search,'%') "
            + "or LOWER(logs.author.lastName) like CONCAT('%',:search,'%')) ")
    Page<ResourceEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                @Param("search") @Nullable String searchParam);

}
