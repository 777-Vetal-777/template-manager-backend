package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.model.file.FileVersionModel;

import java.util.Optional;

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

@Repository
public interface ResourceFileRepository extends JpaRepository<ResourceFileEntity, Long> {
    List<String> SUPPORTED_DEPENDENCY_SORT_FIELDS = List.of("name", "version", "dependencyType", "stage", "directionType");
    List<String> SUPPORTED_SORT_FIELDS = List.of("version", "stage", "modifiedBy", "modifiedOn", "comment");

    ResourceFileEntity findFirstByResource_IdOrderByVersionDesc(Long id);

    Optional<ResourceFileEntity> findFirstByUuid(String uuid);

    String SELECT_CLAUSE = "select file.version as version, max(CONCAT(file.author.firstName, ' ',file.author.lastName)) as modifiedBy, "
            + " max(file.createdOn) as modifiedOn, max(file.comment) as comment, max(file.stage.name) as stage,  max(file.author.firstName) as firstName, max(file.author.lastName) as lastName"
            + " from ResourceFileEntity file "
            + " left join file.stage"
            + " where file.resource.id = :id and ";

    String FILTER_CONDITION = " (:version=0l or file.version is null or file.version=:version) "
            + "and (:modifiedBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or file.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%'))"
            + "and (:stage='' or LOWER(file.stage.name) like CONCAT('%',:stage,'%')) ";

    String GROUP_BY_VERSION = "group by file.version";

    String SEARCH_CONDITION = "and (CAST(file.version as string) like CONCAT('%',:search,'%') "
            + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
            + "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%') "
            + "or LOWER(file.stage.name) like CONCAT('%',:search,'%') ) ";

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + GROUP_BY_VERSION)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("id") Long resourceId,
                                  @Param("version") @Nullable Long version,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("comment") @Nullable String comment,
                                  @Param("stage") @Nullable String stageName);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION + GROUP_BY_VERSION)
    Page<FileVersionModel> search(Pageable pageable,
                                  @Param("id") Long resourceId,
                                  @Param("version") @Nullable Long version,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("comment") @Nullable String comment,
                                  @Param("stage") @Nullable String stageName,
                                  @Param("search") @Nullable String search);

    @Query(value = "select distinct new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(template.name, templateFiles.version, stage.name) "
            + "from ResourceEntity resource "
            + "join resource.latestFile file "
            + "join file.templateFiles templateFiles "
            + "left join templateFiles.instance instance "
            + "left join instance.stage stage "
            + "left join templateFiles.template template "
            + "where "
            //filtering
            + "file.resource.id = :id "
            + "and (:depend='' or LOWER(template.name) like CONCAT('%',:depend,'%')) "
            + "and (:version=0l or templateFiles.version is null or templateFiles.version=:version) "
            + "and (:stage='' or LOWER(stage.name) like CONCAT('%',:stage,'%')) ")
    Page<DependencyModel> filter(Pageable pageable,
                                 @Param("id") Long resourceId,
                                 @Param("depend") @Nullable String depend,
                                 @Param("version") @Nullable Long version,
                                 @Param("stage") @Nullable String stage);

    @Query(value = "select distinct new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(template.name, templateFiles.version, stage.name) "
            + "from ResourceEntity resource "
            + "join resource.latestFile file "
            + "join file.templateFiles templateFiles "
            + "left join templateFiles.instance instance "
            + "left join instance.stage stage "
            + "left join templateFiles.template template "
            + "where "
            //filtering
            + "resource.id = :id "
            + "and (:depend='' or LOWER(template.name) like CONCAT('%',:depend,'%')) "
            + "and (:version=0l or templateFiles.version is null or templateFiles.version=:version) "
            + "and (:stage='' or LOWER(stage.name) like CONCAT('%',:stage,'%')) "
            //search
            + "and LOWER(template.name) like CONCAT('%',:search,'%') "
            + "or CAST(templateFiles.version as string) like CONCAT('%',:search,'%') "
            + "or 'template' like CONCAT('%',:search,'%') "
            + "or LOWER(stage.name) like CONCAT('%',:search,'%')")
    Page<DependencyModel> search(Pageable pageable,
                                 @Param("id") Long resourceId,
                                 @Param("depend") @Nullable String depend,
                                 @Param("version") @Nullable Long version,
                                 @Param("stage") @Nullable String stage,
                                 @Param("search") @Nullable String search);

    @Query(value = "select distinct new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(template.name, templateFiles.version, stage.name) "
            + "from ResourceEntity resource "
            + "join resource.latestFile file "
            + "join file.templateFiles templateFiles "
            + "left join templateFiles.instance instance "
            + "left join instance.stage stage "
            + "left join templateFiles.template template "
            + "where resource.id = :id ")
    List<DependencyModel> searchDependencies(@Param("id") Long resourceId);
}
