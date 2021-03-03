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
    
	String SELECT_FONT = " select resource from  ResourceEntity resource"
			+" join resource.latestFile latestFile"
			+ " where resource.type = :type and resource.name = :name and latestFile.fontName = :fontName";

    String SELECT_CLAUSE = "select resource from ResourceEntity resource "
            + " join resource.latestFile latestFile "
            + " join resource.latestLogRecord latestLogRecord";
    String FILTER_CONDITION = "((:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(latestFile.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or resource.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date))) ";
    String SEARCH_CONDITION = "(LOWER(resource.name) like CONCAT('%',:search,'%') "
            + "or LOWER(latestFile.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(resource.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(resource.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%') ";

    String GROUP_BY_ID_COMMENT_DATE_FIRSTNAME = "group by resource.id, latestFile.comment, latestLogRecord.date, latestLogRecord.author.firstName";
    String PAGEABLE_FILTER_QUERY = SELECT_CLAUSE + " where " + FILTER_CONDITION;
    String PAGEABLE_SEARCH_AND_FILTER_QUERY = PAGEABLE_FILTER_QUERY + " and" + SEARCH_CONDITION;

    Boolean existsByNameEqualsAndTypeEquals(String resourceName, ResourceTypeEnum type);

    Optional<ResourceEntity> findByNameAndType(String resourcesName, ResourceTypeEnum type);

    ResourceEntity findByName(String name);

    @Query(value = PAGEABLE_FILTER_QUERY + GROUP_BY_ID_COMMENT_DATE_FIRSTNAME)
    Page<ResourceEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(value = PAGEABLE_SEARCH_AND_FILTER_QUERY + GROUP_BY_ID_COMMENT_DATE_FIRSTNAME)
    Page<ResourceEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("types") @Nullable List<ResourceTypeEnum> types,
                                @Param("comment") @Nullable String comment,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                @Param("search") @Nullable String searchParam);

    @Query(value = "select resource from ResourceEntity resource "
            + "join resource.latestFile lastFiles "
            + "left join lastFiles.templateFiles templatesFiles "
            + "left join templatesFiles.template template "
            + "where template.id = :id")
    List<ResourceEntity> findAllResourceByTemplateId(@Param("id") Long templateId);
    
	@Query(value = SELECT_FONT)
	Optional<ResourceEntity> getByNameTypeAndFontName(@Param("name") String name, 
													  @Param("type") ResourceTypeEnum type,
													  @Param("fontName") String fontName);

    @Query("select max(CAST(SUBSTR(name, LENGTH(:pattern) + 2, LENGTH(name) - LENGTH(:pattern) - 2 ) as int)) from ResourceEntity where name like CONCAT(:pattern, '(%)')")
    Optional<Integer> findMaxIntegerByNamePattern(@Param("pattern") String pattern);

}
