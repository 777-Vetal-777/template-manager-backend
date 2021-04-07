package com.itextpdf.dito.manager.repository.datasample;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
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
public interface DataSampleRepository extends JpaRepository<DataSampleEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "modifiedBy", "modifiedOn", "comment", "isDefault");

    String DATA_SAMPLE_TABLE_SELECT_CLAUSE = "select ds from DataSampleEntity ds " +
            "join ds.lastDataSampleLog lastLog " +
            "join ds.latestVersion latestFile " +
            "where ds.dataCollection.id = :collectionId ";

    String DATA_SAMPLE_TABLE_FILTER_CONDITION = "and (:name='' or LOWER(ds.name) like CONCAT('%',:name,'%')) "
            + "and (:modifiedBy='' or LOWER(CONCAT(lastLog.author.firstName, ' ',lastLog.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or ds.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:description='' or LOWER(latestFile.comment) like CONCAT('%',:description,'%'))"
            + "and (:isDefault=null or ds.isDefault IS :isDefault) ";

    String DATA_SAMPLE_TABLE_SEARCH_CONDITION = " and (CAST(ds.modifiedOn as string) like CONCAT('%',:search,'%') "
            + "or LOWER(latestFile.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(ds.name) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(lastLog.author.firstName, ' ', lastLog.author.lastName)) like LOWER(CONCAT('%',:search,'%')))";

    @Query(value = DATA_SAMPLE_TABLE_SELECT_CLAUSE + DATA_SAMPLE_TABLE_FILTER_CONDITION)
    Page<DataSampleEntity> filter(Pageable pageable,
                                  @Param("collectionId") @Nullable Long collectionId,
                                  @Param("name") @Nullable String name,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                  @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                  @Param("isDefault") @Nullable Boolean isDefault,
                                  @Param("description") @Nullable String description);

    @Query(value = DATA_SAMPLE_TABLE_SELECT_CLAUSE + DATA_SAMPLE_TABLE_FILTER_CONDITION + DATA_SAMPLE_TABLE_SEARCH_CONDITION)
    Page<DataSampleEntity> search(Pageable pageable,
                                  @Param("collectionId") @Nullable Long collectionId,
                                  @Param("name") @Nullable String name,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                  @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                  @Param("description") @Nullable String description,
                                  @Param("isDefault") @Nullable Boolean isDefault,
                                  @Param("search") @Nullable String search);

    Optional<DataSampleEntity> findByName(String name);

    Boolean existsByName(String name);

    Boolean existsByNameAndDataCollection(String name, DataCollectionEntity dataCollection);

    Boolean existsByDataCollection(DataCollectionEntity dataCollection);

    Optional<List<DataSampleEntity>> findByDataCollection(DataCollectionEntity dataCollection);

    @Query(DATA_SAMPLE_TABLE_SELECT_CLAUSE)
    List<DataSampleEntity> findDataSampleEntitiesByDataCollectionId(@Param("collectionId") Long id);

    @Query("select sample from DataSampleEntity sample "
            + "join sample.latestVersion latestFile "
            + "join sample.dataCollection collection "
            + "join collection.latestVersion version "
            + "left join version.templateFiles templates "
            + "left join templates.template template "
            + "where template.id =:templateId and sample.isDefault = true")
    Optional<DataSampleEntity> findDataSampleByTemplateId(@Param("templateId") Long templateId);

    @Query("select sample from DataSampleEntity sample "
            + "join sample.latestVersion latestFile "
            + "join sample.dataCollection collection "
            + "where collection.id =:dataCollectionId and sample.isDefault = true")
    Optional<DataSampleEntity> findDataSampleByCollectionId(@Param("dataCollectionId") Long dataCollectionId);

    @Query("select sample from DataSampleEntity sample "
            + "join sample.latestVersion latestFile "
            + "left join sample.dataCollection collection "
            + "left join collection.latestVersion version "
            + "left join version.templateFiles templates "
            + "left join templates.template template "
            + "where template.name =:templateName")
    List<DataSampleEntity> findDataSamplesByTemplateName(@Param("templateName") String templateName);

    @Query("select max(CAST(SUBSTR(name, LENGTH(:pattern) + 2, LENGTH(name) - LENGTH(:pattern) - 2 ) as int)) from DataSampleEntity where name like CONCAT(:pattern, '(%)')")
    Optional<Integer> findMaxIntegerByNamePattern(@Param("pattern") String pattern);

}