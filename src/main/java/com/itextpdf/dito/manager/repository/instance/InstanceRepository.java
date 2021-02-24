package com.itextpdf.dito.manager.repository.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "stage", "socket", "createdBy", "createdOn");

    String PAGEABLE_SELECT_CLAUSE = "select i from instance i "
            + "left join i.stage stage ";

    String FILTER_CONDITION = " ((:name='' or LOWER(i.name) like CONCAT('%',:name,'%')) "
            + "and (:socket='' or LOWER(i.socket) like CONCAT('%',:socket,'%')) "
            + "and ((COALESCE(:stages) is not null and LOWER(stage.name) in (:stages)) or (COALESCE(:stages) is null or (LOWER(stage.name) in (:stages))))"
            + "and (cast(:startDate as date) is null or i.createdOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and (:createdBy='' or LOWER(CONCAT(i.createdBy.firstName, ' ', i.createdBy.lastName)) like CONCAT('%',:createdBy,'%')))";
    String SEARCH_CONDITION = "(LOWER(i.name) like CONCAT('%',:search,'%') "
            + " or LOWER(i.socket) like CONCAT('%',:search,'%') "
            + " or LOWER(i.stage.name) like CONCAT('%',:search,'%') "
            + " or LOWER(CONCAT(i.createdBy.firstName, ' ', i.createdBy.lastName)) like CONCAT('%',:search,'%')"
            + " or LOWER(CAST(i.createdOn as string)) like CONCAT('%',:search,'%'))";
    Optional<InstanceEntity> findByName(String name);

    Optional<InstanceEntity> findBySocket(String socket);

    Page<InstanceEntity> findAll(Pageable pageable);

    @Query(PAGEABLE_SELECT_CLAUSE + "where " + FILTER_CONDITION)
    Page<InstanceEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("socket") @Nullable String socket,
                                @Param("createdBy") @Nullable String createdBy,
                                @Param("startDate") @Nullable @Temporal Date startDate,
                                @Param("endDate") @Nullable @Temporal Date endDate,
                                @Param("stages") @Nullable List<String> stages);

    @Query(PAGEABLE_SELECT_CLAUSE + "where " + FILTER_CONDITION + " and " + SEARCH_CONDITION)
    Page<InstanceEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("socket") @Nullable String socket,
                                @Param("createdBy") @Nullable String createdBy,
                                @Param("startDate") @Nullable @Temporal Date startDate,
                                @Param("endDate") @Nullable @Temporal Date endDate,
                                @Param("stages") @Nullable List<String> stages,
                                @Param("search") String searchParam);

    void deleteByName(String name);

    @Query("select i "
            + " from instance i "
            + " left join i.stage stage "
            + " where stage.sequenceOrder is not null and stage.sequenceOrder = 0")
    List<InstanceEntity> getInstancesOnDevStage();
}
