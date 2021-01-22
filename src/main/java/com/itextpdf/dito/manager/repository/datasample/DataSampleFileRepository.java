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

@Repository
public interface DataSampleFileRepository extends JpaRepository<DataSampleFileEntity, Long> {

    String SELECT_CLAUSE = "select file.version as version, CONCAT(file.author.firstName, ' ',file.author.lastName) as modifiedBy, file.createdOn as modifiedOn, " +
            "file.comment as comment, stages.name as stage from DataSampleFileEntity file " +
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
            "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%') ) ";

    @Query(SELECT_CLAUSE + FILTER_CONDITION)
    Page<FileVersionModel> filter(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("version") @Nullable Long version,
                                  @Param("createdBy") @Nullable String createdBy,
                                  @Param("startDate") @Nullable @Temporal Date startDate,
                                  @Param("endDate") @Nullable @Temporal Date endDate,
                                  @Param("stage") @Nullable String stage,
                                  @Param("comment") @Nullable String comment);

    @Query(SELECT_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION)
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