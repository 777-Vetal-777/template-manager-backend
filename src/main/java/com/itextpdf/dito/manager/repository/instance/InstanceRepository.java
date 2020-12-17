package com.itextpdf.dito.manager.repository.instance;

import com.itextpdf.dito.manager.entity.InstanceEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("id", "name", "socket", "createdBy", "createdOn");


    Optional<InstanceEntity> findByName(String name);

    Page<InstanceEntity> findAll(Pageable pageable);

    @Query("select i from instance i "
            + "where (:name='' or LOWER(i.name) like CONCAT('%',:name,'%')) "
            + "and (:socket='' or LOWER(i.socket) like CONCAT('%',:socket,'%')) "
            + "and (cast(:startDate as date) is null or i.createdOn between cast(:startDate as date) and cast(:endDate as date)) "
            + "and ((:createdBy='' or LOWER(i.createdBy.firstName) like CONCAT('%',:createdBy,'%')) "
            + "or (:createdBy='' or LOWER(i.createdBy.lastName) like CONCAT('%',:createdBy,'%'))) "
    )
    Page<InstanceEntity> filter(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("socket") @Nullable String socket,
                                @Param("createdBy") @Nullable String createdBy,
                                @Param("startDate") @Nullable Date startDate,
                                @Param("endDate")@Nullable Date endDate);

    @Query("select i from instance i "
            + "where "
            //filter
            + "("
            + "(:name='' or LOWER(i.name) like CONCAT('%',:name,'%')) "
            + "and (:socket='' or LOWER(i.socket) like CONCAT('%',:socket,'%')) "
            + "and (:startDate is null or i.createdOn between :startDate and :endDate) "
            + "and ((:createdBy='' or LOWER(i.createdBy.firstName) like CONCAT('%',:createdBy,'%')) "
            + "or (:createdBy='' or LOWER(i.createdBy.lastName) like CONCAT('%',:createdBy,'%')))) "
            //search
            + " and (LOWER(i.name) like CONCAT('%',:search,'%') "
            + " or LOWER(i.socket) like CONCAT('%',:search,'%') "
            + " or LOWER(i.createdBy.firstName) like CONCAT('%',:search,'%') "
            + " or LOWER(i.createdBy.lastName) like CONCAT('%',:search,'%')) "
    )
    Page<InstanceEntity> search(Pageable pageable,
                                @Param("name") @Nullable String name,
                                @Param("socket") @Nullable String socket,
                                @Param("createdBy") @Nullable String createdBy,
                                @Param("startDate") @Nullable Date startDate,
                                @Param("endDate") @Nullable Date endDate,
                                @Param("search") String searchParam);

    void deleteByName(String name);
}
