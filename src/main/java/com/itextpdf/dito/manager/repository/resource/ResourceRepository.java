package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.model.resource.MetaInfoModel;
import com.itextpdf.dito.manager.model.resource.ResourceModel;
import com.itextpdf.dito.manager.model.resource.ResourceRoleModel;
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
            + " join resource.latestFile latestFile"
            + " where resource.type = :type and resource.name = :name and latestFile.fontName = :fontName";

    String SELECT_CLAUSE = "select resource from ResourceEntity resource "
            + " join resource.latestFile latestFile "
            + " join resource.latestLogRecord latestLogRecord";
    String FILTER_CONDITION = "((:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(latestFile.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or resource.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date))) ";
    String SEARCH_CONDITION = " (LOWER(resource.name) like CONCAT('%',:search,'%') "
            + "or LOWER(latestFile.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(resource.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(resource.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%') ";

    String GROUP_BY_ID_COMMENT_DATE_FIRSTNAME = "group by resource.id, latestFile.comment, latestLogRecord.date, latestLogRecord.author.firstName";
    String PAGEABLE_FILTER_QUERY = SELECT_CLAUSE + " where " + FILTER_CONDITION;
    String PAGEABLE_SEARCH_AND_FILTER_QUERY = PAGEABLE_FILTER_QUERY + " and" + SEARCH_CONDITION;

    String SELECT_CLAUSE_MODEL = "select max(resource.name)             as resourceName," +
            " max(resource.id) as id," +
            " max(resource.description) as description," +
            " max(resource.type)        as type," +
            " max(resource.createdOn)  as createdOn," +
            " max(users.firstName)     as authorFirstName," +
            " max(users.lastName)      as authorLastName," +
            " max(file.version)         as version," +
            " max(file.comment)         as comment," +
            " file.deployed             as deployed," +
            " max(log.date)             as modifiedOn," +
            " max(CONCAT(logAuthorFirstName.firstName,' ', logAuthorFirstName.lastName))     as modifiedBy," +
            " max(logAuthorFirstName.firstName) as authorFirstName from ResourceEntity  resource " +
            "left join resource.createdBy users " +
            "left join resource.latestFile file " +
            "left join resource.latestLogRecord log " +
            "left join log.author logAuthorFirstName";
    String FILTER_CONDITION_MODEL = " ((:name='' or LOWER(resource.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or resource.type in (:types)) "
            + "and (:comment='' or LOWER(file.comment) like LOWER(CONCAT('%',:comment,'%'))) "
            + "and (:modifiedBy='' or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or resource.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date)))";

    String SEARCH_CONDITIONAL_MODEL = " (LOWER(resource.name) like CONCAT('%',:search,'%') "
            + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(resource.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(resource.latestLogRecord.author.firstName, ' ', resource.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(resource.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%') ";

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

    @Query(SELECT_CLAUSE_MODEL + " where " + FILTER_CONDITION_MODEL + "group by resource.id, file.deployed, log.author.firstName, file.comment ")
    Page<ResourceModel> getResourceModelFilter(Pageable pageable,
                                               @Param("name") @Nullable String name,
                                               @Param("types") @Nullable List<ResourceTypeEnum> types,
                                               @Param("comment") @Nullable String comment,
                                               @Param("modifiedBy") @Nullable String modifiedBy,
                                               @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                               @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(SELECT_CLAUSE_MODEL + " where " + FILTER_CONDITION_MODEL + " and" + SEARCH_CONDITIONAL_MODEL +
            "  group by resource.id, file.deployed, log.author.firstName, file.comment ")
    Page<ResourceModel> getResourceModelSearch(Pageable pageable,
                                               @Param("name") @Nullable String name,
                                               @Param("types") @Nullable List<ResourceTypeEnum> types,
                                               @Param("comment") @Nullable String comment,
                                               @Param("modifiedBy") @Nullable String modifiedBy,
                                               @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                               @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                               @Param("search") @Nullable String searchParam);

    @Query(value = "select id, type, resourceId, roleName," +
            " E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE," +
            " E8_US66_DELETE_RESOURCE_IMAGE," +
            " E8_US66_1_DELETE_RESOURCE_FONT," +
            " E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT," +
            " E8_US58_EDIT_RESOURCE_METADATA_FONT," +
            " E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT," +
            " E8_US66_2_DELETE_RESOURCE_STYLESHEET," +
            " E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET," +
            " E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET," +
            " E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET" +
            " from (select r.id as id, r.type as type, max(resource.id) as resourceId, max(r.name) as roleName," +
            " max(case when p.name = 'E8_US55_EDIT_RESOURCE_METADATA_IMAGE' then 'true' else 'false' end) as E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " max(case when p.name = 'E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " max(case when p.name = 'E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE," +
            " max(case when p.name = 'E8_US66_DELETE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US66_DELETE_RESOURCE_IMAGE," +
            " max(case when p.name = 'E8_US66_1_DELETE_RESOURCE_FONT' then 'true' else 'false' end) as E8_US66_1_DELETE_RESOURCE_FONT," +
            " max(case when p.name = 'E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT' then 'true' else 'false' end) as E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT," +
            " max(case when p.name = 'E8_US58_EDIT_RESOURCE_METADATA_FONT' then 'true' else 'false' end) as E8_US58_EDIT_RESOURCE_METADATA_FONT," +
            " max(case when p.name = 'E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT' then 'true' else 'false' end) as E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT," +
            " max(case when p.name = 'E8_US66_2_DELETE_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US66_2_DELETE_RESOURCE_STYLESHEET," +
            " max(case when p.name = 'E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET," +
            " max(case when p.name = 'E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET' then 'true' else 'false' end) as E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET," +
            " max(case when p.name = 'E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET" +
            " from {h-schema}resource" +
            " join {h-schema}resource_role rr on resource.id = rr.resource_id" +
            " join {h-schema}role r on r.id = rr.role_id and r.master = false" +
            " left join {h-schema}role_permission rp on rp.role_id = r.id" +
            " left join {h-schema}permission p on p.id = rp.permission_id group by r.id) as rolesTable  where  resourceId in (:listId)", nativeQuery = true)
    List<ResourceRoleModel> getRoles(@Param("listId") List<Long> listId);

    @Query("select files.uuid as uuid, files.fileName as fileName, files.fontName as fontType, res.id as resourceId from ResourceEntity res join res.latestFile files where res.id in (:listId)")
    List<MetaInfoModel> getMetaInfo(@Param("listId") List<Long> listId);

    @Query("select distinct resource from ResourceEntity resource "
            + "join fetch resource.latestFile latestFile "
            + "where resource.uuid is null")
    List<ResourceEntity> findByUuidNull();

    Optional<ResourceEntity> findByUuid(String uuid);

}
