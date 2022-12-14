package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.model.template.TemplateModel;
import com.itextpdf.dito.manager.model.template.TemplateRoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

	String UNION_ALL = " union all";
	String LEFT_JOIN_STAGE_ON_STAGE_ID = " left join {h-schema}stage stage on instance.stage_id = stage.id";
	String LEFT_JOIN_INSTANCE_ON_INSTANCE_ID = " left join {h-schema}instance as instance on tfi.instance_id = instance.id";
	String LEFT_JOIN_TEMPLATE_FILE_INSTANCE_ON_TEMPLATE_FILE_ID = " left join {h-schema}template_file_instance as tfi on tfi.template_file_id = templateFile.id";

    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type", "dataCollection", "modifiedBy", "modifiedOn");
    List<String> SUPPORTED_DEPENDENCY_SORT_FIELDS = List.of("name", "version", "dependencyType", "stage", "directionType");

    String TEMPLATE_TABLE_SELECT_CLAUSE = "select template.name as templateName, template.id as id, template.type as type, template.uuid as uuid, data.name as dataCollection, CONCAT(firstAuthorLog.firstName,' ',firstAuthorLog.lastName) as createdBy, "
            + "template.firstLogRecord.date as createdOn, CONCAT(lastAuthorLog.firstName,' ',lastAuthorLog.lastName) as author, lastAuthorLog.firstName as authorFirstName, "
            + "template.latestFile.version as version, template.latestLogRecord.date as lastUpdate, template.latestFile.comment as comment from TemplateEntity template "
            + "join template.firstLogRecord.author firstAuthorLog "
            + "join template.latestLogRecord.author lastAuthorLog "
            + "left join template.latestFile templateFile "
            + "left join templateFile.dataCollectionFile dataCollectionFile "
            + "left join dataCollectionFile.dataCollection data ";

    String FILTER_CONDITION = "(:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or template.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date)) "
            + "and ((data.name is not null and (LOWER(data.name) like CONCAT('%',:dataCollectionName,'%')))" +
            " or (data.name is null and ('no data collection' like CONCAT('%',:dataCollectionName,'%'))))";

    String SEARCH_CONDITION = "(LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or LOWER(data.name) like CONCAT('%',:search,'%') "
            + "or (data.name is null and 'no data collection' like CONCAT('%',:search,'%')) "
            + "or LOWER(template.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%') "
            + "or CAST(CAST(template.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%')) ";

    String SELECT_CLAUSE_TEMPLATE_HARD_DEPENDENCIES =
            " select max(template_file2.version) as version, template2.name as name, 'HARD' as directionType, 'TEMPLATE' as dependencyType, 'TEMPLATE' as dependencyTypePluralName, max(stage.name) as stage"
            + " from {h-schema}template"
            + " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from {h-schema}template_file tf where tf.template_id = template.id and tf.version = (select max(version) from {h-schema}template_file tf2 where tf2.template_id = template.id))"
            + " join {h-schema}template_file_part ON template_file_part.template_file_part_id = templateFile.id"
            + " join {h-schema}template_file template_file2 ON template_file2.id = template_file_part.template_file_id"
            + " join {h-schema}template template2 on template2.id = template_file2.template_id"
            + LEFT_JOIN_TEMPLATE_FILE_INSTANCE_ON_TEMPLATE_FILE_ID
            + LEFT_JOIN_INSTANCE_ON_INSTANCE_ID
            + LEFT_JOIN_STAGE_ON_STAGE_ID
            + " where template.id = :id group by template2.name";

    String DEPENDENCY_SUBQUERY =
            "(select max(resourceFile.version) as version, max(resource.name) as name, 'SOFT' as directionType, max(resource.type) as dependencyType, max(resource.type) as dependencyTypePluralName,  max(stage.name) as stage"
            + " from {h-schema}template as template"
            + " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from manager.template_file tf where tf.template_id = template.id and tf.version = (select max(version) from manager.template_file tf2 where tf2.template_id = template.id))"
            + " join {h-schema}resource_file_template_file rft on rft.template_file_id = templateFile.id"
            + " join {h-schema}resource_file resourceFile on resourceFile.id = rft.resource_file_id"
            + " join {h-schema}resource resource on resource.id = resourceFile.resource_id"
            + " left join {h-schema}template_file_instance tfi on tfi.template_file_id = templateFile.id"
            + " left join {h-schema}instance instance on tfi.instance_id = instance.id"
            + LEFT_JOIN_STAGE_ON_STAGE_ID
            + " where template.id = :id group by resource.name"
            + UNION_ALL
            + " select max(dataFile.version) as version, max(data.name) as name, 'SOFT' as directionType, 'DATA_COLLECTION' as dependencyType, 'data collection' as dependencyTypePluralName, max(stage.name) as stage"
            + " from {h-schema}template as template"
            + " join {h-schema}template_file as templateFile ON templateFile.id = (select max(id) from manager.template_file tf where tf.template_id = template.id and tf.version = (select max(version) from manager.template_file tf2 where tf2.template_id = template.id))"
            + " join {h-schema}data_collection_file as dataFile on templateFile.data_collection_file_id = dataFile.id"
            + " join {h-schema}data_collection as data on dataFile.data_collection_id = data.id"
            + LEFT_JOIN_TEMPLATE_FILE_INSTANCE_ON_TEMPLATE_FILE_ID
            + LEFT_JOIN_INSTANCE_ON_INSTANCE_ID
            + LEFT_JOIN_STAGE_ON_STAGE_ID
            + " where template.id = :id group by data.name"
            + UNION_ALL
            + " select max(template_file2.version) as version, template2.name as name, 'SOFT' as directionType, 'TEMPLATE' as dependencyType, 'TEMPLATE' as dependencyTypePluralName, max(stage.name) as stage"
            + " from {h-schema}template"
            + " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from {h-schema}template_file tf where tf.template_id = template.id and tf.version = (select max(version) from {h-schema}template_file tf2 where tf2.template_id = template.id))"
            + " join {h-schema}template_file_part ON template_file_part.template_file_id = templateFile.id"
            + " join {h-schema}template_file template_file2 ON template_file2.id = template_file_part.template_file_part_id"
            + " join {h-schema}template template2 on template2.id = template_file2.template_id"
            + LEFT_JOIN_TEMPLATE_FILE_INSTANCE_ON_TEMPLATE_FILE_ID
            + LEFT_JOIN_INSTANCE_ON_INSTANCE_ID
            + LEFT_JOIN_STAGE_ON_STAGE_ID
            + " where template.id = :id group by template2.name"
            + UNION_ALL + SELECT_CLAUSE_TEMPLATE_HARD_DEPENDENCIES + ") as dependency";

    String DEPENDENCY_COUNT_QUERY = "select count(*) from " + DEPENDENCY_SUBQUERY;

    String DEPENDENCY_QUERY = "select version, name, LOWER(name) as lower_name, directionType, dependencyType, dependencyTypePluralName, stage, LOWER(stage) as lower_stage from " + DEPENDENCY_SUBQUERY;

    String FILTER_DEPENDENCIES = " where ((:depend='' or LOWER(name) like CONCAT('%',:depend,'%')) " +
            " and (:version = 0 or version=:version) " +
            " and (:directionType='' or LOWER(directionType) like LOWER(CONCAT('%',:directionType,'%')))" +
            " and (COALESCE(:stages) is null or LOWER(stage) in (:stages)) " +
            " and (COALESCE(:dependencyTypes) is null or LOWER(dependencyType) in (:dependencyTypes)))";

    String SEARCH_DEPENDENCIES = " and ( LOWER(name) like CONCAT('%',:search,'%') " +
            " or LOWER(directionType) like CONCAT('%',:search,'%')" +
            " or CAST(version as VARCHAR(10)) like CONCAT('%',:search,'%')" +
            " or LOWER(dependencyTypePluralName) like CONCAT('%',:search,'%')" +
            " or LOWER(stage) like CONCAT('%',:search,'%') )";

    String TEMPLATE_TABLE_LIST_SELECT_CLAUSE = "select template from TemplateEntity template "
            + " join fetch template.latestFile templateFile "
            + " left join fetch templateFile.dataCollectionFile dataCollectionFile "
            + " left join fetch dataCollectionFile.dataCollection dataCollection ";

    String FILTER_BY_TYPES_CONDITION = "(COALESCE(:types) is null or template.type in (:types)) ";

    String FILTER_LIST_CONDITION = FILTER_BY_TYPES_CONDITION
            + " and (dataCollection.name is null or dataCollection.name = :dataCollectionName)";

    Optional<TemplateEntity> findByName(String name);

    @Query(value = TEMPLATE_TABLE_SELECT_CLAUSE + "where " + FILTER_CONDITION)
    Page<TemplateModel> filter(Pageable pageable,
                               @Param("name") @Nullable String name,
                               @Param("modifiedBy") @Nullable String modifiedBy,
                               @Param("types") @Nullable List<TemplateTypeEnum> types,
                               @Param("dataCollectionName") @Nullable String dataCollectionName,
                               @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                               @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(value = TEMPLATE_TABLE_SELECT_CLAUSE + "where " + FILTER_CONDITION + " and " + SEARCH_CONDITION)
    Page<TemplateModel> search(Pageable pageable,
                               @Param("name") @Nullable String name,
                               @Param("modifiedBy") @Nullable String modifiedBy,
                               @Param("types") @Nullable List<TemplateTypeEnum> types,
                               @Param("dataCollectionName") @Nullable String dataCollectionName,
                               @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                               @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                               @Param("search") String searchParam);

    @Override
    @Query(value = "select template from TemplateEntity template "
            + "join template.files file ")
    Page<TemplateEntity> findAll(Pageable pageable);

    @Query(value = "select template from TemplateEntity template "
            + "left join template.files files "
            + "left join files.resourceFiles resourceFiles "
            + "where resourceFiles.resource.id = :resourceId "
            + "group by template.id")
    List<TemplateEntity> findTemplatesByResourceId(@Param("resourceId") Long resourceId);

    @Query(value = "select latestTemplateFile from TemplateEntity template "
            + "left join template.latestFile latestTemplateFile "
            + "left join latestTemplateFile.dataCollectionFile collectionFile "
            + "left join collectionFile.dataCollection collection "
            + "where collection.id = :dataCollectionId "
            + "group by latestTemplateFile.id")
    List<TemplateFileEntity> findTemplatesFilesByDataCollectionId(@Param("dataCollectionId") Long dataCollectionId);

    @Query(value = DEPENDENCY_QUERY + FILTER_DEPENDENCIES
            , countQuery = DEPENDENCY_COUNT_QUERY + FILTER_DEPENDENCIES
            , nativeQuery = true)
    Page<DependencyModel> filter(Pageable pageable,
                                 @Param("id") Long templateId,
                                 @Param("depend") String depend,
                                 @Param("version") Long version,
                                 @Param("directionType") String directionType,
                                 @Param("dependencyTypes") List<String> dependencyType,
                                 @Param("stages") List<String> stages);

    @Query(value = DEPENDENCY_QUERY + FILTER_DEPENDENCIES + SEARCH_DEPENDENCIES
            , countQuery = DEPENDENCY_COUNT_QUERY + FILTER_DEPENDENCIES + SEARCH_DEPENDENCIES
            , nativeQuery = true)
    Page<DependencyModel> search(Pageable pageable,
                                 @Param("id") Long templateId,
                                 @Param("depend") String depend,
                                 @Param("version") Long version,
                                 @Param("directionType") String directionType,
                                 @Param("dependencyTypes") List<String> dependencyType,
                                 @Param("stages") List<String> stages,
                                 @Param("search") String search);

    @Query(value = DEPENDENCY_QUERY, countQuery = DEPENDENCY_COUNT_QUERY, nativeQuery = true)
    List<DependencyModel> list(@Param("id") Long templateId);

    @Modifying
    @Query("update TemplateEntity t set t.blockedBy = null, t.blockedAt=null where t.blockedAt< :blockExpirationDate")
    void unlockTemplatesWithExpiredBlockTime(@Param("blockExpirationDate") @Temporal Date blockExpirationDate);

    @Query(TEMPLATE_TABLE_LIST_SELECT_CLAUSE + " where " + FILTER_LIST_CONDITION)
    List<TemplateEntity> getListTemplates(@Param("types") @Nullable List<TemplateTypeEnum> types,
                                          @Param("dataCollectionName") @Nullable String dataCollectionName);

    @Query(TEMPLATE_TABLE_LIST_SELECT_CLAUSE + " where " + FILTER_BY_TYPES_CONDITION)
    List<TemplateEntity> getListTemplates(@Param("types") @Nullable List<TemplateTypeEnum> types);

    @Query(TEMPLATE_TABLE_LIST_SELECT_CLAUSE
            + " where template.name in (:names)")
    List<TemplateEntity> getTemplatesWithLatestFileByName(List<String> names);

    @Query("select template from TemplateEntity template "
            + "join fetch template.latestFile file "
            + "join fetch file.compositions compositions "
            + "join fetch compositions.composition composition "
            + "join composition.template compositionTemplate "
            + "left join fetch file.dataCollectionFile dataCollectionFile "
            + "left join fetch dataCollectionFile.dataCollection dataCollection "
            + "where compositionTemplate.latestFile.version = composition.version and compositionTemplate.id = :id order by compositions.id")
    List<TemplateEntity> getTemplatesPartsByTemplateId(@Param("id") Long templateId);

    @Query(value = SELECT_CLAUSE_TEMPLATE_HARD_DEPENDENCIES, nativeQuery = true)
    List<DependencyModel> getTemplateHardRelations(@Param("id") Long templateId);

    @Query("select max(CAST(SUBSTR(name, LENGTH(:pattern) + 2, LENGTH(name) - LENGTH(:pattern) - 2 ) as int)) from TemplateEntity where name like CONCAT(:pattern, '(%)')")
    Optional<Integer> findMaxIntegerByNamePattern(@Param("pattern") String pattern);

    @Query(value = "select file from TemplateFileEntity file "
            + "left join file.resourceFiles resources "
            + "join resources.resource resource "
            + "where resource.id = :resourceId ")
    List<TemplateFileEntity> findTemplateFilesByResourceId(@Param("resourceId") Long resourceId);

    @Query("select data from TemplateFileEntity data "
            + "join fetch data.parts parts "
            + "join fetch parts.part part "
            + "join part.template compositionTemplate "
            + "where compositionTemplate.id = :id")
    List<TemplateFileEntity> getAllTemplateFileVersions(@Param("id") Long templateId);

    @Query(value = "select id, type, templateId, roleName," +
            " E9_US75_EDIT_TEMPLATE_METADATA_STANDARD," +
            " E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD," +
            " E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE," +
            " E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE," +
            " E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED," +
            " E9_US81_PREVIEW_TEMPLATE_STANDARD," +
            " E9_US24_EXPORT_TEMPLATE_DATA" +
            " from (select r.id as id, r.type as type, max(template.id) as templateId, max(r.name) as roleName," +
            " max(case when p.name = 'E9_US75_EDIT_TEMPLATE_METADATA_STANDARD' then 'true' else 'false' end) as E9_US75_EDIT_TEMPLATE_METADATA_STANDARD," +
            " max(case when p.name = 'E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD' then 'true' else 'false' end) as E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD," +
            " max(case when p.name = 'E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE' then 'true' else 'false' end) as E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE," +
            " max(case when p.name = 'E9_US24_EXPORT_TEMPLATE_DATA' then 'true' else 'false' end) as E9_US24_EXPORT_TEMPLATE_DATA," +
            " max(case when p.name = 'E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE' then 'true' else 'false' end) as E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE," +
            " max(case when p.name = 'E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED' then 'true' else 'false' end) as E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED," +
            " max(case when p.name = 'E9_US81_PREVIEW_TEMPLATE_STANDARD' then 'true' else 'false' end) as E9_US81_PREVIEW_TEMPLATE_STANDARD" +
            " from {h-schema}template" +
            " join {h-schema}template_role rr on template.id = rr.template_id" +
            " join {h-schema}role r on r.id = rr.role_id and r.master = false" +
            " left join {h-schema}role_permission rp on rp.role_id = r.id" +
            " left join {h-schema}permission p on p.id = rp.permission_id group by r.id) as rolesTable  where  templateId in (:listId)", nativeQuery = true)
    List<TemplateRoleModel> getRoles(@Param("listId") List<Long> listId);

    List<TemplateEntity> findByUuidNull();

    Optional<TemplateEntity> findByUuid(String uuid);
}