package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
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
public interface DataCollectionFileRepository extends JpaRepository<DataCollectionFileEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("version", "modifiedBy", "modifiedOn", "comment", "stage");
    List<String> SUPPORTED_DEPENDENCY_SORT_FIELDS = List.of("name", "version", "dependencyType", "stage", "directionType");

    String SELECT_CLAUSE = "select file.version as version, CONCAT(file.author.firstName, ' ',file.author.lastName) as modifiedBy, "
            + " file.createdOn as modifiedOn, file.comment as comment, file.stage.name as stage "
            + " from DataCollectionFileEntity file "
            + " left join file.stage "
            + " where file.dataCollection.id = :id and ";

    String FILTER_CONDITION = "(:version=0l or file.version=:version) "
            + "and (:createdBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:createdBy,'%')) "
            + "and (cast(:startDate as date) is null or file.createdOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%')) "
            + "and (COALESCE(:stages) is null or LOWER(file.stage.name) in (:stages)) ";

    String SEARCH_CONDITION = "( CAST(file.version as string) like CONCAT('%',:search,'%') "
            + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%')) "
            + "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%') "
            + "or LOWER(file.stage.name) like CONCAT('%',:search,'%') ) ";

    String SELECT_DEPENDENCIES_CLAUSE = "select new com.itextpdf.dito.manager.model.datacollection.DataCollectionDependencyModel(template.name, lastTemplateFile.version, stage.name) "
            + "from TemplateEntity template "
            + "join template.latestFile lastTemplateFile "
            + "left join lastTemplateFile.dataCollectionFile file "
            + "left join lastTemplateFile.instance instance "
            + "left join instance.stage stage "
            + "where ";

    String FILTERING_DEPENDENCIES_CONDITION = " file.dataCollection.id = :id "
            + "and (:dependencyName='' or LOWER(template.name) like CONCAT('%',:dependencyName,'%')) "
            + "and (:version=0l or lastTemplateFile.version=:version) "
            + "and (CONCAT(:direction) is null or 'hard' in (:direction)) "
            + "and (COALESCE(:stages) is null or LOWER(stage.name) in (:stages)) ";

    String SEARCH_DEPENDENCIES_CONDITION = "( LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or cast(lastTemplateFile.version as text) like CONCAT('%',:search,'%') "
            + "or 'template' like CONCAT('%',:search,'%') "
            + "or 'hard' like CONCAT('%',:search,'%') "
            + "or LOWER(stage.name) like CONCAT('%',:search,'%') )";

    DataCollectionFileEntity findFirstByDataCollection_IdOrderByVersionDesc(Long id);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("id") Long dataCollectionId,
                                  @Param("version") @Nullable Long version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stages") @Nullable List<String> stageName,
                                  @Param("comment") @Nullable String comment);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + " and " + SEARCH_CONDITION)
    Page<FileVersionModel> search(Pageable pageable,
                                  @Param("id") Long dataCollectionId,
                                  @Param("version") @Nullable Long version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stages") @Nullable List<String> stageName,
                                  @Param("comment") @Nullable String comment,
                                  @Param("search") @Nullable String search);

    @Query(value = SELECT_DEPENDENCIES_CLAUSE + FILTERING_DEPENDENCIES_CONDITION)
    Page<DependencyModel> filter(Pageable pageable,
                                 @Param("id") Long dataCollectionId,
                                 @Param("dependencyName") @Nullable String dependencyName,
                                 @Param("version") @Nullable Long version,
                                 @Param("direction") @Nullable List<String> direction,
                                 @Param("stages") @Nullable List<String> stages);

    @Query(value = SELECT_DEPENDENCIES_CLAUSE + FILTERING_DEPENDENCIES_CONDITION + " and " + SEARCH_DEPENDENCIES_CONDITION)
    Page<DependencyModel> search(Pageable pageable,
                                 @Param("id") Long dataCollectionId,
                                 @Param("dependencyName") @Nullable String dependencyName,
                                 @Param("version") @Nullable Long version,
                                 @Param("direction") @Nullable List<String> direction,
                                 @Param("stages") @Nullable List<String> stages,
                                 @Param("search") @Nullable String search);

    @Query(value = SELECT_DEPENDENCIES_CLAUSE + "file.dataCollection.id =:id ")
    List<DependencyModel> searchDependencyOfDataCollection(@Param("id") Long dataCollectionId);
  
    Optional<DataCollectionFileEntity> findByVersionAndDataCollection(Long version, DataCollectionEntity dataCollectionEntity);

    Optional<DataCollectionFileEntity> findByVersionAndDataCollection_Id(Long version, Long id);
}
