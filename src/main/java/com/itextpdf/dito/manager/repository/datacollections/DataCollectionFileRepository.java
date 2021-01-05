package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
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
public interface DataCollectionFileRepository extends JpaRepository<DataCollectionFileEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS =List.of("version", "modifiedBy", "modifiedOn", "comment", "deploymentStatus");

    String SELECT_CLAUSE = "select file from DataCollectionFileEntity file where file.dataCollection.id = :id and ";

    String FILTER_CONDITION = "(:version=0l or file.version=:version) "
            + "and (:createdBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:createdBy,'%')) "
            + "and (cast(:startDate as date) is null or file.createdOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%'))";

    String SEARCH_CONDITION = "CAST(file.version as string) like CONCAT('%',:search,'%') "
            + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
            + "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%') ";


    DataCollectionFileEntity findFirstByDataCollection_IdOrderByVersionDesc(Long id);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION)
    Page<DataCollectionFileEntity> filter(Pageable pageable,
                                    @Param("id") Long dataCollectionId,
                                    @Param("version") @Nullable Long version,
                                    @Param("createdBy") @Nullable String createdBy,
                                    @Param("startDate") @Nullable @Temporal Date startDate,
                                    @Param("endDate") @Nullable @Temporal Date endDate,
                                    @Param("comment") @Nullable String comment);

    @Query(value = SELECT_CLAUSE + FILTER_CONDITION + " and " + SEARCH_CONDITION)
    Page<DataCollectionFileEntity> search(Pageable pageable,
                                    @Param("id") Long dataCollectionId,
                                    @Param("version") @Nullable Long version,
                                    @Param("createdBy") @Nullable String createdBy,
                                    @Param("startDate") @Nullable @Temporal Date startDate,
                                    @Param("endDate") @Nullable @Temporal Date endDate,
                                    @Param("comment") @Nullable String comment,
                                    @Param("search") @Nullable String search);
}
