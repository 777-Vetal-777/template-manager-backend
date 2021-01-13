package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;

import java.util.List;
import java.util.Optional;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "type", "users", "permissions");

    Optional<RoleEntity> findByNameAndMasterTrue(String name);

    List<RoleEntity> findByNameAndMasterFalse(String name);

    RoleEntity findByNameAndMasterFalseAndResources(String name, ResourceEntity resourceEntity);

    RoleEntity findByNameAndMasterFalseAndDataCollections(String name, DataCollectionEntity dataCollectionEntity);

    RoleEntity findByNameAndMasterFalseAndTemplates(String name, TemplateEntity templateEntity);

    @Query(value = "select role from RoleEntity role"
            + " left join role.permissions permission"
            + " left join role.resources resource"
            + " where resource = :resourceEntity"
            + " and role.master = false"
            + " and (COALESCE(:types) is null or role.type in (:types))"
            + " and (:name is null or LOWER(role.name) like CONCAT('%',:name,'%'))"
            + " group by (role.id)")
    Page<RoleEntity> findAllByResourcesAndMasterFalse(Pageable pageable, ResourceEntity resourceEntity, @Param("name") @Nullable String name,
                                                      @Param("types") @Nullable List<RoleTypeEnum> types);

    @Query(value = "select role from RoleEntity role"
            + " left join role.templates template"
            + " left join role.permissions permissions"
            + " where template = :templateEntity"
            + " and role.master = false"
            + " and (:name is null or LOWER(role.name) like CONCAT('%',:name,'%'))"
            + " group by (role.id)")
    Page<RoleEntity> findAllByTemplatesAndMasterFalse(Pageable pageable, TemplateEntity templateEntity, @Param("name") @Nullable String name);

    @Query(value = "select role from RoleEntity  role"
            + " left join role.dataCollections dataCollection"
            + " left join role.permissions permission"
            + " where dataCollection = :dataCollection"
            + " and role.master = false"
            + " and(:name is null or LOWER(role.name) like CONCAT('%',:name,'%'))"
            + " group by (role.id)")
    Page<RoleEntity> findAllByDataCollectionsAndMasterFalse(Pageable pageable, @Param("dataCollection") DataCollectionEntity dataCollectionEntity,
                                                            @Param("name") String name);


    void deleteByNameAndMasterFalseAndResources(String name, ResourceEntity resourceEntity);

    @Query(value = "select count(r) from RoleEntity r "
            + "where r.name = :name and r.master = true ")
    Integer countByNameAndMasterTrue(String name);

    @Query(value = "select role from RoleEntity role "
            + "left join role.users user "
            + "where (:name='' or LOWER(role.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or role.type in (:types)) "
            + "and role.master = true "
            + "group by (role.id, role.type)")
    Page<RoleEntity> filter(Pageable pageable,
                            @Param("name") @Nullable String name,
                            @Param("types") @Nullable List<RoleTypeEnum> types);

    @Query(value = "select role from RoleEntity role "
            + "left join role.users user "
            + "where  "
            //search
            + "(LOWER(user.email) like  LOWER(CONCAT('%',:search,'%')) "
            + " or LOWER(role.type) like  LOWER(CONCAT('%',:search,'%')) "
            + " or  LOWER(role.name) like  LOWER(CONCAT('%',:search,'%')) "
            + " and role.master = true)"
            //filtering
            + " and (:name is null or LOWER(role.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or role.type in (:types)) "
            + "group by (role.id, role.type)")
    Page<RoleEntity> search(Pageable pageable,
                            @Param("name") @Nullable String name,
                            @Param("types") @Nullable List<RoleTypeEnum> types,
                            @Param("search") String search);

}
