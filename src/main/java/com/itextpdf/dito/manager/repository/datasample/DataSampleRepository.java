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

    String DATA_SAMPLE_TABLE_SELECT_CLAUSE = "select ds from DataSampleEntity ds where ";

    String DATA_SAMPLE_TABLE_FILTER_CONDITION = "(:name='' or LOWER(ds.name) like CONCAT('%',:name,'%')) "
            + "and (:modifiedBy='' or LOWER(CONCAT(ds.author.firstName, ' ', ds.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or ds.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:comment='' or LOWER(ds.comment) like CONCAT('%',:comment,'%'))"
            + "and (:isDefault=null or ds.isDefault IS :isDefault) "; 

    String DATA_SAMPLE_TABLE_SEARCH_CONDITION = " and (CAST(ds.modifiedOn as string) like CONCAT('%',:search,'%') "
            + "or LOWER(ds.comment) like CONCAT('%',:search,'%') "
            + "or LOWER(ds.name) like CONCAT('%',:search,'%') "
            + "or LOWER(CONCAT(ds.author.firstName, ' ', ds.author.lastName)) like LOWER(CONCAT('%',:search,'%')))";

    @Query(value = DATA_SAMPLE_TABLE_SELECT_CLAUSE + DATA_SAMPLE_TABLE_FILTER_CONDITION)
    Page<DataSampleEntity> filter(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                  @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                  @Param("isDefault") @Nullable Boolean isDefault,
                                  @Param("comment") @Nullable String comment);

    @Query(value = DATA_SAMPLE_TABLE_SELECT_CLAUSE + DATA_SAMPLE_TABLE_FILTER_CONDITION + DATA_SAMPLE_TABLE_SEARCH_CONDITION)
    Page<DataSampleEntity> search(Pageable pageable,
                                  @Param("name") @Nullable String name,
                                  @Param("modifiedBy") @Nullable String modifiedBy,
                                  @Param("startDate") @Nullable @Temporal Date modifiedOnStartDate,
                                  @Param("endDate") @Nullable @Temporal Date modifiedOnEndDate,
                                  @Param("comment") @Nullable String comment,
                                  @Param("search") @Nullable String search);

    Optional<DataSampleEntity> findByName(String name);
    
    Boolean existsByName(String name);
    
    Boolean existsByDataCollection(DataCollectionEntity dataCollection);
    
    Optional<List<DataSampleEntity>> findByDataCollection(DataCollectionEntity dataCollection);
    
}