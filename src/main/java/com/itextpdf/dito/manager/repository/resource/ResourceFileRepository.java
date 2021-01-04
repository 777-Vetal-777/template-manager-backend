package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.model.resource.ResourceDependencyModel;
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
        List<String> SUPPORTED_DEPENDENCY_SORT_FIELDS = List.of("name", "version", "dependencyType", "active", "directionType");
        List<String> SUPPORTED_SORT_FIELDS = List.of("version", "deployed", "modifiedBy", "modifiedOn", "comment");

        ResourceFileEntity findFirstByResource_IdOrderByVersionDesc(Long id);

        @Query(value = "select file from ResourceFileEntity file "
                + "where "
                //filtering
                + "file.resource.id = :id "
                + "and (:version=0l or file.version is null or file.version=:version) "
                + "and (:modifiedBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
                + "and (cast(:startDate as date) is null or file.modifiedOn between cast(:startDate as date) and cast(:endDate as date)) "
                + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%'))"
                + "and (:deployed=null or file.deployed IS :deployed) ")
        Page<ResourceFileEntity> filter(Pageable pageable,
                                        @Param("id") Long resourceId,
                                        @Param("version") @Nullable Long version,
                                        @Param("modifiedBy") @Nullable String modifiedBy,
                                        @Param("startDate") @Nullable @Temporal Date startDate,
                                        @Param("endDate") @Nullable @Temporal Date endDate,
                                        @Param("comment") @Nullable String comment,
                                        @Param("deployed") @Nullable Boolean deployed);

        @Query(value = "select file from ResourceFileEntity file "
                + "where "
                //filtering
                + "file.resource.id = :id "
                + "and (:version=0l or file.version is null or file.version=:version) "
                + "and (:modifiedBy='' or LOWER(CONCAT(file.author.firstName, ' ',file.author.lastName)) like CONCAT('%',:modifiedBy,'%')) "
                + "and (cast(:startDate as date) is null or file.createdOn between cast(:startDate as date) and cast(:endDate as date)) "
                + "and (:comment='' or LOWER(file.comment) like CONCAT('%',:comment,'%'))"
                + "and (:deployed=null or file.deployed IS :deployed) "
                //search
                + "and (CAST(file.version as string) like CONCAT('%',:search,'%') "
                + "or LOWER(file.comment) like CONCAT('%',:search,'%') "
                + "or LOWER(CONCAT(file.author.firstName, ' ', file.author.lastName)) like LOWER(CONCAT('%',:search,'%'))"
                + "or LOWER(CAST(CAST(file.createdOn as date) as string)) like CONCAT('%',:search,'%')) ")
        Page<ResourceFileEntity> search(Pageable pageable,
                                        @Param("id") Long resourceId,
                                        @Param("version") @Nullable Long version,
                                        @Param("modifiedBy") @Nullable String modifiedBy,
                                        @Param("startDate") @Nullable @Temporal Date startDate,
                                        @Param("endDate") @Nullable @Temporal Date endDate,
                                        @Param("comment") @Nullable String comment,
                                        @Param("deployed") @Nullable Boolean deployed,
                                        @Param("search") @Nullable String search);

        @Query(value = "select new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(templates.name, file.version, file.deployed) "
                + "from ResourceFileEntity file "
                + "join file.templates templates "
                + "where "
                //filtering
                + "file.resource.id = :id "
                + "and (:depend='' or LOWER(templates.name) like CONCAT('%',:depend,'%')) "
                + "and (:version=0l or file.version is null or file.version=:version) "
                + "and (:type is null or file.resource.type = :type) "
                + "and (:deployed=null or file.deployed IS :deployed) ")
        Page<ResourceDependencyModel> filter(Pageable pageable,
                                             @Param("id") Long resourceId,
                                             @Param("depend") @Nullable String depend,
                                             @Param("version") @Nullable Long version,
                                             @Param("type") @Nullable ResourceTypeEnum type,
                                             @Param("deployed") @Nullable Boolean deployed);

        @Query(value = "select new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(templates.name, file.version, file.deployed) "
                + "from ResourceFileEntity file "
                + "join file.templates templates "
                + "where "
                //filtering
                + "file.resource.id = :id "
                + "and (:depend='' or LOWER(templates.name) like CONCAT('%',:depend,'%')) "
                + "and (:version=0l or file.version is null or file.version=:version) "
                + "and (:type is null or file.resource.type = :type) "
                + "and (:deployed=null or file.deployed IS :deployed) "
                //search
                +"and LOWER(templates.name) like CONCAT('%',:search,'%') "
                +"or CAST(file.version as string) like CONCAT('%',:search,'%') "
                +"or LOWER(CAST(file.resource.type as string)) like CONCAT('%',:search,'%') "
                +"or (LOWER(file.resource.type) like CONCAT('%',:search,'%')) ")
        Page<ResourceDependencyModel> search(Pageable pageable,
                                             @Param("id") Long resourceId,
                                             @Param("depend") @Nullable String depend,
                                             @Param("version") @Nullable Long version,
                                             @Param("type") @Nullable ResourceTypeEnum type,
                                             @Param("deployed") @Nullable Boolean deployed,
                                             @Param("search") @Nullable String search);

        @Query(value = "select new com.itextpdf.dito.manager.model.resource.ResourceDependencyModel(templates.name, file.version, file.deployed) "
                + "from ResourceFileEntity file "
                + "join file.templates templates "
                + "where file.resource.id =:id ")
        List<ResourceDependencyModel> search(@Param("id") Long resourceId);
}
