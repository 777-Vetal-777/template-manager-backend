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
            "E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE",
            "E8_US66_1_DELETE_RESOURCE_FONT",
            "E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT",
            "E8_US58_EDIT_RESOURCE_METADATA_FONT",
            "E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT",
            "E8_US66_2_DELETE_RESOURCE_STYLESHEET",
            "E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET",
            "E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET",
            "E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET");


    String SELECT_FIELDS_CLAUSE = "select name, type, resourceType, lower(name) as lower_name, " +
            " E8_US66_DELETE_RESOURCE_IMAGE," +
            " E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE," +
            " E8_US66_1_DELETE_RESOURCE_FONT," +
            " E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT," +
            " E8_US58_EDIT_RESOURCE_METADATA_FONT," +
            " E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT," +
            " E8_US66_2_DELETE_RESOURCE_STYLESHEET," +
            " E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET," +
            " E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET," +
            " E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET" +
            " from ";

    String SELECT_COUNT_CLAUSE = "select count(*) from ";

    String SUBQUERY_CLAUSE = "(select role.name as name, max(role.type) as type, max(resource.type) as resourceType," +
            " max(case when permission.name = 'E8_US66_DELETE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US66_DELETE_RESOURCE_IMAGE," +
            " max(case when permission.name = 'E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US62_CREATE_NEW_VERSION_OF_RESOURCE_IMAGE," +
            " max(case when permission.name = 'E8_US55_EDIT_RESOURCE_METADATA_IMAGE' then 'true' else 'false' end) as E8_US55_EDIT_RESOURCE_METADATA_IMAGE," +
            " max(case when permission.name = 'E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE' then 'true' else 'false' end) as E8_US65_ROLL_BACK_OF_THE_RESOURCE_IMAGE," +
            " max(case when permission.name = 'E8_US66_1_DELETE_RESOURCE_FONT' then 'true' else 'false' end) as E8_US66_1_DELETE_RESOURCE_FONT," +
            " max(case when permission.name = 'E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT' then 'true' else 'false' end) as E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT," +
            " max(case when permission.name = 'E8_US58_EDIT_RESOURCE_METADATA_FONT' then 'true' else 'false' end) as E8_US58_EDIT_RESOURCE_METADATA_FONT," +
            " max(case when permission.name = 'E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT' then 'true' else 'false' end) as E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT," +
            " max(case when permission.name = 'E8_US66_2_DELETE_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US66_2_DELETE_RESOURCE_STYLESHEET," +
            " max(case when permission.name = 'E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET," +
            " max(case when permission.name = 'E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET' then 'true' else 'false' end) as E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET," +
            " max(case when permission.name = 'E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET' then 'true' else 'false' end) as E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET" +
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
            " and (:DELETE_RESOURCE_IMAGE_BOOLEAN = '' or :DELETE_RESOURCE_IMAGE_BOOLEAN= E8_US66_DELETE_RESOURCE_IMAGE) " +
            " and (:EDIT_RESOURCE_METADATA_FONT_BOOLEAN = '' or :EDIT_RESOURCE_METADATA_FONT_BOOLEAN= E8_US58_EDIT_RESOURCE_METADATA_FONT)" +
            " and (:CREATE_NEW_VERSION_OF_RESOURCE_FONT_BOOLEAN = '' or :CREATE_NEW_VERSION_OF_RESOURCE_FONT_BOOLEAN= E8_US62_1_CREATE_NEW_VERSION_OF_RESOURCE_FONT)" +
            " and (:ROLL_BACK_OF_THE_RESOURCE_FONT_BOOLEAN = '' or :ROLL_BACK_OF_THE_RESOURCE_FONT_BOOLEAN= E8_US65_1_ROLL_BACK_OF_THE_RESOURCE_FONT)" +
            " and (:DELETE_RESOURCE_FONT_BOOLEAN = '' or :DELETE_RESOURCE_FONT_BOOLEAN= E8_US66_1_DELETE_RESOURCE_FONT) " +
            " and (:EDIT_RESOURCE_METADATA_STYLESHEET_BOOLEAN = '' or :EDIT_RESOURCE_METADATA_STYLESHEET_BOOLEAN= E8_US61_EDIT_RESOURCE_METADATA_STYLESHEET)" +
            " and (:CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET_BOOLEAN = '' or :CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET_BOOLEAN= E8_US63_CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET)" +
            " and (:ROLL_BACK_OF_THE_RESOURCE_STYLESHEET_BOOLEAN = '' or :ROLL_BACK_OF_THE_RESOURCE_STYLESHEET_BOOLEAN= E8_US65_2_ROLL_BACK_OF_THE_RESOURCE_STYLESHEET)" +
            " and (:DELETE_RESOURCE_STYLESHEET_BOOLEAN = '' or :DELETE_RESOURCE_STYLESHEET_BOOLEAN= E8_US66_2_DELETE_RESOURCE_STYLESHEET) ";

    String SEARCH_CONDITION = " and LOWER(name) like CONCAT('%',:search,'%')";

    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            nativeQuery = true)
    Page<ResourcePermissionModel> filter(Pageable pageable, @Param("resourceId") Long resourceId,
                                         @Param("role_names") List<String> names,
                                         @Param("EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN") String editResourceImage,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN") String createVersionImage,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN") String rollBackImage,
                                         @Param("DELETE_RESOURCE_IMAGE_BOOLEAN") String deleteResourceImage,
                                         @Param("EDIT_RESOURCE_METADATA_FONT_BOOLEAN") String editResourceFont,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_FONT_BOOLEAN") String createVersionFont,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_FONT_BOOLEAN") String rollBackFont,
                                         @Param("DELETE_RESOURCE_FONT_BOOLEAN") String deleteResourceFont,
                                         @Param("EDIT_RESOURCE_METADATA_STYLESHEET_BOOLEAN") String editResourceStylesheet,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET_BOOLEAN") String createVersionStylesheet,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_STYLESHEET_BOOLEAN") String rollBackStylesheet,
                                         @Param("DELETE_RESOURCE_STYLESHEET_BOOLEAN") String deleteResourceStylesheet);


    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            nativeQuery = true)
    Page<ResourcePermissionModel> search(Pageable pageable, @Param("resourceId") Long resourceId,
                                         @Param("role_names") List<String> names,
                                         @Param("EDIT_RESOURCE_METADATA_IMAGE_BOOLEAN") String editResourceImage,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_IMAGE_BOOLEAN") String createVersionImage,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_IMAGE_BOOLEAN") String rollBackImage,
                                         @Param("DELETE_RESOURCE_IMAGE_BOOLEAN") String deleteResourceImage,
                                         @Param("EDIT_RESOURCE_METADATA_FONT_BOOLEAN") String editResourceFont,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_FONT_BOOLEAN") String createVersionFont,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_FONT_BOOLEAN") String rollBackFont,
                                         @Param("DELETE_RESOURCE_FONT_BOOLEAN") String deleteResourceFont,
                                         @Param("EDIT_RESOURCE_METADATA_STYLESHEET_BOOLEAN") String editResourceStylesheet,
                                         @Param("CREATE_NEW_VERSION_OF_RESOURCE_STYLESHEET_BOOLEAN") String createVersionStylesheet,
                                         @Param("ROLL_BACK_OF_THE_RESOURCE_STYLESHEET_BOOLEAN") String rollBackStylesheet,
                                         @Param("DELETE_RESOURCE_STYLESHEET_BOOLEAN") String deleteResourceStylesheet,
                                         @Param("search") String search);
}