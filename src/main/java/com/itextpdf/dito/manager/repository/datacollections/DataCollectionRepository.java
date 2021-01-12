package com.itextpdf.dito.manager.repository.datacollections;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface DataCollectionRepository extends JpaRepository<DataCollectionEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "type", "modifiedOn", "modifiedBy");

    Optional<DataCollectionEntity> findByName(String name);

    Boolean existsByName(String name);

    @Query(value = "select dc from DataCollectionEntity dc "
            + "join dc.lastDataCollectionLog lastlog "
            + "where "
            //filtering
            + "(:name='' or LOWER(dc.name) like CONCAT('%',:name,'%')) "
            + "and (:modifiedBy='' or LOWER(CONCAT(lastlog.author.firstName, ' ',lastlog.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or dc.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (COALESCE(:types) is null or dc.type in (:types))")
    Page<DataCollectionEntity> filter(Pageable pageable,
                                      @Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types);

    @Query(value = "select dc from DataCollectionEntity dc "
            + "join dc.lastDataCollectionLog lastlog "
            + "where "
            //filtering
            + "(:name='' or LOWER(dc.name) like CONCAT('%',:name,'%')) "
            + "and (:modifiedBy='' or LOWER(CONCAT(lastlog.author.firstName, ' ', lastlog.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
            + "and (cast(:startDate as date) is null or dc.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (COALESCE(:types) is null or dc.type in (:types)) "
            //search
            + "and (LOWER(dc.name) like LOWER(CONCAT('%',:search,'%')) "
            + "or LOWER(CONCAT(dc.author.firstName, ' ', dc.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
            + "or LOWER(dc.type) like LOWER(CONCAT('%',:search,'%'))) "
            + "or CAST(CAST(dc.modifiedOn as date) as string) like CONCAT('%',:search,'%')")
    Page<DataCollectionEntity> search(Pageable pageable,
                                      @Param("name") @Nullable String name,
                                      @Param("modifiedBy") @Nullable String modifiedBy,
                                      @Param("startDate") @Nullable @Temporal Date modificationStartDate,
                                      @Param("endDate") @Nullable @Temporal Date modificationEndDate,
                                      @Param("types") @Nullable List<DataCollectionType> types,
                                      @Param("search") String searchParam);

}
