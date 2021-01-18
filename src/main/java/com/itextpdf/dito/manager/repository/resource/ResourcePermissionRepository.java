package com.itextpdf.dito.manager.repository.resource;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.resource.ResourcePermissionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcePermissionRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name",
            "E8_US66_DELETE_RESOURCE_IMAGE",
            "E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE",
            "E8_US55_EDIT_RESOURCE_METADATA_IMAGE",
            "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE");


    String SELECT_FIELDS_CLAUSE = "select name, type," +
            " E8_US66_DELETE_RESOURCE_IMAGE," +
            " E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE" +
            " from ";

    String SELECT_COUNT_CLAUSE = "select count(*) from ";

    String SUBQUERY_CLAUSE = "(select role.name as name, max(role.type) as type," +
            " max(case when permission.name = 'E8_US66_DELETE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US66_DELETE_RESOURCE_IMAGE," +
            " max(case when permission.name = 'E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " max(case when permission.name = 'E8_US55_EDIT_RESOURCE_METADATA_IMAGE' then 'true' else 'false' end) as E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " max(case when permission.name = 'E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE" +
            " from manager.role role" +
            " left join {h-schema}role_permission on role.id = role_permission.role_id " +
            " left join {h-schema}permission on role_permission.permission_id = permission.id" +
            " join {h-schema}resource_role rr on role.id = rr.role_id" +
            " join {h-schema}resource resource on rr.resource_id = resource.id" +
            " where resource.id = :resourceId" +
            " and role.master = false" +
            " group by role.name) as resourcePermissions" +
            " where";

    String FILTER_CONDITION = " (COALESCE(:role_names) is null or CAST(name as text) in (:role_names))" +
            " and (:EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN = '' or :EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN= E8_US55_EDIT_RESOURCE_METADATA_IMAGE)" +
            " and (:CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN = '' or :CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN= E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE)" +
            " and (:ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN = '' or :ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN= E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE)" +
            " and (:DELETE_RESOURCE_IMAGE_BOOLEAN = '' or :DELETE_RESOURCE_IMAGE_BOOLEAN= E8_US66_DELETE_RESOURCE_IMAGE) ";

    String SEARCH_CONDITION = " and LOWER(name) like CONCAT('%',:search,'%')";

    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            nativeQuery = true)
    Page<ResourcePermissionModel> filter(Pageable pageable, @Param("resourceId") Long resourceId,
                                         @Param("role_names") List<String> names,
                                         @Param("EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN") String editResource,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN") String createVersion,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN") String rollBack,
                                         @Param("DELETE_RESOURCE_IMAGE_BOOLEAN") String deleteResource);


    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            nativeQuery = true)
    Page<ResourcePermissionModel> search(Pageable pageable,
                                         @Param("resourceId") Long resourceId,
                                         @Param("role_names") List<String> names,
                                         @Param("EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN") String editResource,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN") String createVersion,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN") String rollBack,
                                         @Param("DELETE_RESOURCE_IMAGE_BOOLEAN") String deleteResource,
                                         @Param("search") String search);
}
