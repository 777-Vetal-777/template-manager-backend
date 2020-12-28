package com.itextpdf.dito.manager.repository.role;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.RoleTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;

import javax.management.relation.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "type", "users");

    Optional<RoleEntity> findByName(String name);

    Page<RoleEntity> findAllByResources(Pageable pageable, ResourceEntity resourceEntity);

    @Query(value = "select role from RoleEntity role "
            + "left join role.users user "
            + "where (:name='' or LOWER(role.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or role.type in (:types)) "
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
            + " or  LOWER(role.name) like  LOWER(CONCAT('%',:search,'%'))) "
            //filtering
            + " and (:name is null or LOWER(role.name) like CONCAT('%',:name,'%')) "
            + "and (COALESCE(:types) is null or role.type in (:types)) "
            + "group by (role.id, role.type)")
    Page<RoleEntity> search(Pageable pageable,
                            @Param("name") @Nullable String name,
                            @Param("types") @Nullable List<RoleTypeEnum> types,
                            @Param("search") String search);
}
