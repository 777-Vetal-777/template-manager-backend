package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
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
public interface TemplateFileRepository extends JpaRepository<TemplateFileEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("version", "stage", "modifiedBy", "modifiedOn", "comment");

    String SELECT_CLAUSE = "select file.version as version, CONCAT(file.author.firstName, ' ',file.author.lastName) as modifiedBy, "
            + " file.createdOn as modifiedOn, file.comment as comment, file.stage.name as stage "
            + " from TemplateFileEntity file "
            + " left join file.stage "
            + " where file.template.id = :id and ";

    String FILTER_CONDITION = "(:version is null or file.version=:version) "
            + "and (:modifiedBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or file.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:stage='' or LOWER(file.stage.name) like CONCAT('%',:stage,'%')) "
            + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%')) ";

    String SEARCH_CONDITION = "(CAST(file.version as string) like CONCAT('%',:search,'%') "
            + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%')) "
            + "or LOWER(file.stage.name) like CONCAT('%',:search,'%') "
            + "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%')) ";


    TemplateFileEntity findFirstByTemplate_IdOrderByVersionDesc(Long id);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("id") Long templateId,
                                  @Param("version") @Nullable Long version,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("comment") @Nullable String comment,
                                  @Param("stage") @Nullable String stageName);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + " and " + SEARCH_CONDITION)
    Page<FileVersionModel> search(Pageable pageable,
                                  @Param("id") Long templateId,
                                  @Param("version") @Nullable Long version,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("comment") @Nullable String comment,
                                  @Param("stage") @Nullable String stageName,
                                  @Param("search") @Nullable String search);

    Optional<TemplateFileEntity> findByVersionAndTemplate(Long version, TemplateEntity templateEntity);

    Optional<TemplateFileEntity> findByStageAndTemplate(StageEntity stageEntity, TemplateEntity templateEntity);

    @Query(value = "select count(templateFile) from TemplateFileEntity templateFile "
            + "where size(templateFile.compositions) > 0 and templateFile.template.name=:name")
    Integer countTemplateVersionsUsedInCompositions(String name);
}
