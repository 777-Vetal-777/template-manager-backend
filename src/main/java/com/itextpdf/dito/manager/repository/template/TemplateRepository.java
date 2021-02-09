package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
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
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "type", "dataCollection", "modifiedBy", "modifiedOn");

    String TEMPLATE_TABLE_SELECT_CLAUSE = "select template from TemplateEntity template "
            + "left join template.latestFile templateFile "
            + "left join templateFile.dataCollectionFile dataCollectionFile "
            + "left join dataCollectionFile.dataCollection dataCollection  ";

    String FILTER_CONDITION = "(:name='' or LOWER(template.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or template.type in (:types)) "
            + "and (:modifiedBy='' or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or template.latestLogRecord.date between cast(:startDate as date) and cast(:endDate as date)) "
            + "and ((:dataCollectionName <> '' and (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))"
            + "or (:dataCollectionName = '' and (dataCollection.name is null or  (LOWER(dataCollection.name) like CONCAT('%',:dataCollectionName,'%')))))";

    String SEARCH_CONDITION = "(LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or LOWER(dataCollection.name) like CONCAT('%',:search,'%') "
            + "or LOWER(template.type) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(template.latestLogRecord.author.firstName, ' ', template.latestLogRecord.author.lastName)) like CONCAT('%',:search,'%')) "
            + "or CAST(CAST(template.latestLogRecord.date as date) as string) like CONCAT('%',:search,'%') ";

    String DEPENDENCY_QUERY = "select version, name, directionType, dependencyType, stage from (select max(resourceFile.version) as version, max(resource.name) as name, 'SOFT' as directionType, max(resource.type) as dependencyType,  max(stage.name) as stage" +
            " from {h-schema}template as template" +
            " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from manager.template_file tf where tf.template_id = template.id and tf.version = (select max(version) from manager.template_file tf2 where tf2.template_id = template.id))" +
            " join {h-schema}resource_file_template_file rft on rft.template_file_id = templateFile.id" +
            " join {h-schema}resource_file resourceFile on resourceFile.id = rft.resource_file_id" +
            " join {h-schema}resource resource on resource.id = resourceFile.resource_id" +
            " left join {h-schema}template_file_instance tfi on tfi.template_file_id = templateFile.id" +
            " left join {h-schema}instance instance on tfi.instance_id = instance.id" +
            " left join {h-schema}stage stage on instance.stage_id = stage.id" +
            " where template.id = :id group by resource.name" +
            " union all" +
            " select max(dataFile.version) as version, max(data.name) as name, 'SOFT' as directionType, 'DATA_COLLECTION' as dependencyType, max(stage.name) as stage" +
            " from {h-schema}template as template" +
            " join {h-schema}template_file as templateFile ON templateFile.id = (select max(id) from manager.template_file tf where tf.template_id = template.id and tf.version = (select max(version) from manager.template_file tf2 where tf2.template_id = template.id))" +
            " join {h-schema}data_collection_file as dataFile on templateFile.data_collection_file_id = dataFile.id" +
            " join {h-schema}data_collection as data on dataFile.data_collection_id = data.id" +
            " left join {h-schema}template_file_instance as tfi on tfi.template_file_id = templateFile.id" +
            " left join {h-schema}instance as instance on tfi.instance_id = instance.id" +
            " left join {h-schema}stage stage on instance.stage_id = stage.id" +
            " where template.id = :id group by data.name" +
            " union all" +
            " select max(template_file2.version) as version, template2.name as name, 'SOFT' as directionType, 'TEMPLATE' as dependencyType, max(stage.name) as stage" +
            " from {h-schema}template" +
            " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from {h-schema}template_file tf where tf.template_id = template.id and tf.version = (select max(version) from {h-schema}template_file tf2 where tf2.template_id = template.id))" +
            " join {h-schema}template_file_part ON template_file_part.template_file_id = templateFile.id" +
            " join {h-schema}template_file template_file2 ON template_file2.id = template_file_part.template_file_part_id" +
            " join {h-schema}template template2 on template2.id = template_file2.template_id" +
            " left join {h-schema}template_file_instance as tfi on tfi.template_file_id = templateFile.id" +
            " left join {h-schema}instance as instance on tfi.instance_id = instance.id" +
            " left join {h-schema}stage stage on instance.stage_id = stage.id" +
            " where template.id = :id group by template2.name"+
            " union all" +
            " select max(template_file2.version) as version, template2.name as name, 'HARD' as directionType, 'TEMPLATE' as dependencyType, max(stage.name) as stage" +
            " from {h-schema}template" +
            " join {h-schema}template_file templateFile ON templateFile.id = (select max(id) from {h-schema}template_file tf where tf.template_id = template.id and tf.version = (select max(version) from {h-schema}template_file tf2 where tf2.template_id = template.id))" +
            " join {h-schema}template_file_part ON template_file_part.template_file_part_id = templateFile.id" +
            " join {h-schema}template_file template_file2 ON template_file2.id = template_file_part.template_file_id" +
            " join {h-schema}template template2 on template2.id = template_file2.template_id" +
            " left join {h-schema}template_file_instance as tfi on tfi.template_file_id = templateFile.id" +
            " left join {h-schema}instance as instance on tfi.instance_id = instance.id" +
            " left join {h-schema}stage stage on instance.stage_id = stage.id" +
            " where template.id = :id group by template2.name"+
            ") as dependency";

    String FILTER_DEPENDENCIES = " where ((:depend='' or LOWER(name) like CONCAT('%',:depend,'%')) " +
            " and (:version = 0 or version is null or version=:version) " +
            " and (:directionType='' or LOWER(directionType) like LOWER(CONCAT('%',:directionType,'%')))" +
            " and (COALESCE(:dependencyTypes) is null or dependencyType in (:dependencyTypes)))";

    String FILTER_AND_SEARCH_DEPENDENCIES = DEPENDENCY_QUERY + FILTER_DEPENDENCIES + " and (name like CONCAT('%',:search,'%')) " +
            " or (LOWER(directionType) like CONCAT('%',:search,'%'))" +
            " or (CAST(version as VARCHAR(10)) like CONCAT('%',:search,'%'))" +
            " or (LOWER(dependencyType) like CONCAT('%',:search,'%'))" +
            " or (LOWER(stage) like CONCAT('%',:search,'%'))";

    String TEMPLATE_TABLE_LIST_SELECT_CLAUSE = "select template from TemplateEntity template "
            + " join fetch template.latestFile templateFile "
            + " left join fetch templateFile.dataCollectionFile dataCollectionFile "
            + " left join fetch dataCollectionFile.dataCollection dataCollection ";

    String FILTER_LIST_CONDITION = "(COALESCE(:types) is null or template.type in (:types)) "
            + " and (dataCollection.name is null or dataCollection.name = :dataCollectionName)";

    Optional<TemplateEntity> findByName(String name);

    @Query(value = TEMPLATE_TABLE_SELECT_CLAUSE + " where " + FILTER_CONDITION)
    Page<TemplateEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("modifiedBy") @Nullable String modifiedBy,
                                @Param("types") @Nullable List<TemplateTypeEnum> types,
                                @Param("dataCollectionName") @Nullable String dataCollectionName,
                                @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate);

    @Query(value = TEMPLATE_TABLE_SELECT_CLAUSE + "where " + FILTER_CONDITION + " and " + SEARCH_CONDITION)
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
            + "left join template.files files "
            + "left join files.resourceFiles resourceFiles "
            + "where resourceFiles.resource.id = :resourceId "
            + "group by template.id")
    List<TemplateEntity> findTemplatesByResourceId(@Param("resourceId") Long resourceId);

    @Query(value = "select template from TemplateEntity template "
            + "left join template.latestFile latestTemplateFile "
            + "left join latestTemplateFile.dataCollectionFile collectionFile "
            + "left join collectionFile.dataCollection collection "
            + "where collection.id = :dataCollectionId "
            + "group by template.id")
    List<TemplateEntity> findTemplatesByDataCollectionId(@Param("dataCollectionId") Long dataCollectionId);

    @Query(value = "select latestTemplateFile from TemplateEntity template "
            + "left join template.latestFile latestTemplateFile "
            + "left join latestTemplateFile.dataCollectionFile collectionFile "
            + "left join collectionFile.dataCollection collection "
            + "where collection.id = :dataCollectionId "
            + "group by latestTemplateFile.id")
    List<TemplateFileEntity> findTemplatesFilesByDataCollectionId(@Param("dataCollectionId") Long dataCollectionId);

    @Query(value = DEPENDENCY_QUERY + FILTER_DEPENDENCIES
            , nativeQuery = true)
    Page<DependencyModel> filter(Pageable pageable,
                                 @Param("id") Long templateId,
                                 @Param("depend") String depend,
                                 @Param("version") Long version,
                                 @Param("directionType") String directionType,
                                 @Param("dependencyTypes") List<String> dependencyType);


    @Query(value = FILTER_AND_SEARCH_DEPENDENCIES,
            nativeQuery = true)
    Page<DependencyModel> search(Pageable pageable,
                                 @Param("id") Long templateId,
                                 @Param("depend") String depend,
                                 @Param("version") Long version,
                                 @Param("directionType") String directionType,
                                 @Param("dependencyTypes") List<String> dependencyType,
                                 @Param("search") String search);

    @Query(value = DEPENDENCY_QUERY, nativeQuery = true)
    List<DependencyModel> list(@Param("id") Long templateId);

    @Modifying
    @Query("update TemplateEntity t set t.blockedBy = null, t.blockedAt=null where t.blockedAt< :blockExpirationDate")
    void unlockTemplatesWithExpiredBlockTime(@Param("blockExpirationDate") @Temporal Date blockExpirationDate);

    @Query(TEMPLATE_TABLE_LIST_SELECT_CLAUSE + " where " + FILTER_LIST_CONDITION)
    List<TemplateEntity> getListTemplates(@Param("types") @Nullable List<TemplateTypeEnum> types,
                                          @Param("dataCollectionName") @Nullable String dataCollectionName);

    @Query(TEMPLATE_TABLE_LIST_SELECT_CLAUSE
            + " where template.name in (:names)")
    List<TemplateEntity> getTemplatesWithLatestFileByName(List<String> names);


    @Query("select template from TemplateEntity template "
            + "join fetch template.latestFile file "
            + "join fetch file.compositions compositions "
            + "join fetch compositions.composition composition "
            + "left join fetch file.dataCollectionFile dataCollectionFile "
            + "left join fetch dataCollectionFile.dataCollection dataCollection "
            + "where composition.template.id = :id")
    List<TemplateEntity> getTemplatesPartsByTemplateId(@Param("id") Long templateId);
}
