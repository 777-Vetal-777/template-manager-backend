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

    String SELECT_CLAUSE = "select resource from ResourceEntity resource "
            + "left join resource.resourceFiles files ";

    String FILTER_CONDITION = "((:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(files.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(resource.latestLogRecord.author.firstName) like CONCAT('%',:modifiedBy,'%')  or LOWER(resource.latestLogRecord.author.lastName) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or resource.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date))) ";

    String SEARCH_CONDITION = "(LOWER(resource.name) like CONCAT('%',:search,'%') "
            + "or LOWER(files.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(resource.latestLogRecord.author.firstName) like CONCAT('%',:search,'%') "
            + "or LOWER(resource.latestLogRecord.author.lastName) like CONCAT('%',:search,'%')) ";

    String PAGEABLE_FILTER_QUERY = SELECT_CLAUSE + " where " + FILTER_CONDITION;
    String PAGEABLE_SEARCH_AND_FILTER_QUERY = PAGEABLE_FILTER_QUERY + " and" + SEARCH_CONDITION;

    Boolean existsByNameEqualsAndTypeEquals(String resourceName, ResourceTypeEnum type);

    Optional<ResourceEntity> findByNameAndType(String resourcesName, ResourceTypeEnum type);

    ResourceEntity findByName(String name);

    @Query(value = PAGEABLE_FILTER_QUERY)
    Page<ResourceEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(value = PAGEABLE_SEARCH_AND_FILTER_QUERY)
    Page<ResourceEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                @Param("search") @Nullable String searchParam);

}
