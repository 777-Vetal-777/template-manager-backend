package com.itextpdf.dito.manager.repository.datasample;

import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;
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

@Repository
public interface DataSampleFileRepository extends JpaRepository<DataSampleFileEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("version", "modifiedBy", "modifiedOn", "comment", "stage");

    String SELECT_CLAUSE = "select file.version as version, CONCAT(max(file.author.firstName), ' ',max(file.author.lastName)) as modifiedBy, max(file.createdOn) as modifiedOn, " +
            "max(file.comment) as comment, max(stages.name) as stage, max(file.author.firstName) as firstName from DataSampleFileEntity file " +
            "left join file.dataSample.dataCollection.latestVersion.templateFiles templateFiles " +
            "left join templateFiles.instance instance " +
            "left join instance.stage stages " +
            "where file.dataSample.name = :name and ";

    String FILTER_CONDITION = "(:version =0L or file.version=:version) " +
            "and (:createdBy='' or  LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:createdBy,'%')) " +
            "and (cast(:startDate as date) is null or file.createdOn between cast(:startDate as date) and cast(:endDate as date)) " +
            "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%')) " +
            "and (:stage='' or LOWER(stages.name) like CONCAT('%',:stage,'%')) ";

    String SEARCH_CONDITION = "and ( CAST(file.version as string) like CONCAT('%',:search,'%') " +
            "or LOWER(file.comment) like CONCAT('%',:search,'%') " +
            "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%')) " +
            "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%') ) " +
            "or LOWER(stages.name) like CONCAT('%',:search,'%')";

    String GROUP_BY_VERSION = "group by file.version";

    @Query(SELECT_CLAUSE + FILTER_CONDITION + GROUP_BY_VERSION)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("version") @Nullable Long version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stage") @Nullable String stage,
                                  @Param("comment") @Nullable String comment);

    @Query(SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION + GROUP_BY_VERSION)
    Page<FileVersionModel> search(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("version") @Nullable Long version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stage") @Nullable String stage,
                                  @Param("comment") @Nullable String comment,
                                  @Param("search") @Nullable String search);
}