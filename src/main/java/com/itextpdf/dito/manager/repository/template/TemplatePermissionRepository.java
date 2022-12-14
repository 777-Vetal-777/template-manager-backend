package com.itextpdf.dito.manager.repository.template;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplatePermissionRepository extends JpaRepository<TemplateEntity, Long> {
    List<String> SUPPORTED_SORT_FIELDS = List.of("name", "E9_US75_EDIT_TEMPLATE_METADATA_STANDARD", "E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD",
            "E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE", "E9_US81_PREVIEW_TEMPLATE_STANDARD", "E9_US24_EXPORT_TEMPLATE_DATA", "E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE",
            "E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED");

    String SELECT_FIELDS_CLAUSE = "select name, type, templateType, lower(name) as lower_name, "
            + " E9_US75_EDIT_TEMPLATE_METADATA_STANDARD, "
            + " E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD, "
            + " E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE, "
            + " E9_US81_PREVIEW_TEMPLATE_STANDARD, "
            + " E9_US24_EXPORT_TEMPLATE_DATA, "
            + " E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE,"
            + " E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED"
            + " from ";

    String SELECT_COUNT_CLAUSE = "select count(*) from ";

    String SUBQUERY_CLAUSE = "(select r.name, max(r.type) as type, max(template.type) as templateType,"
            + " max(case when p.name = 'E9_US75_EDIT_TEMPLATE_METADATA_STANDARD' then 'true' else 'false' end) as E9_US75_EDIT_TEMPLATE_METADATA_STANDARD, "
            + " max(case when p.name = 'E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD' then 'true' else 'false' end) as E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD, "
            + " max(case when p.name = 'E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE' then 'true' else 'false' end) as E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE, "
            + " max(case when p.name = 'E9_US81_PREVIEW_TEMPLATE_STANDARD' then 'true' else 'false' end) as E9_US81_PREVIEW_TEMPLATE_STANDARD, "
            + " max(case when p.name = 'E9_US24_EXPORT_TEMPLATE_DATA' then 'true' else 'false' end) as E9_US24_EXPORT_TEMPLATE_DATA, "
            + " max(case when p.name = 'E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE' then 'true' else 'false' end) as E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE, "
            + " max(case when p.name = 'E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED' then 'true' else 'false' end) as E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED "
            + "  from {h-schema}template "
            + "  join {h-schema}template_role t on template.id = t.template_id "
            + "  join {h-schema}role r on r.id = t.role_id and r.master = false "
            + "  left join {h-schema}role_permission rp on rp.role_id = r.id "
            + "  left join {h-schema}permission p on p.id = rp.permission_id "
            + " where template.name = :name "
            + " group by r.name) as rolesTable "
            + "where ";

    String FILTER_CONDITION = "(COALESCE(:role_name) is null or LOWER(CAST(name as text)) in (:role_name))"
            + "  and (:edit_template='' or :edit_template=E9_US75_EDIT_TEMPLATE_METADATA_STANDARD) "
            + "  and (:new_version_template_standard='' or :new_version_template_standard=E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD) "
            + "  and (:rollback_template_standard='' or :rollback_template_standard=E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE) "
            + "  and (:preview_template='' or :preview_template=E9_US81_PREVIEW_TEMPLATE_STANDARD) "
            + "  and (:export_template='' or :export_template=E9_US24_EXPORT_TEMPLATE_DATA)"
            + "  and (:new_version_template_composition='' or :new_version_template_composition=E9_US77_CREATE_NEW_VERSION_OF_TEMPLATE_COMPOSED)"
            + "  and (:rollback_template_composition='' or :rollback_template_composition=E9_US100_ROLL_BACK_OF_THE_COMPOSITION_TEMPLATE)";

    String SEARCH_CONDITION = " and LOWER(name) like CONCAT('%',:search,'%')";

    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION,
            nativeQuery = true)
    Page<TemplatePermissionsModel> filter(Pageable pageable,
                                          @Param("name") String templateName,
                                          @Param("role_name") List<String> roleName,
                                          @Param("edit_template") String editTemplate,
                                          @Param("new_version_template_standard") String newVersionTemplateStandard,
                                          @Param("rollback_template_standard") String rollBackStandard,
                                          @Param("preview_template") String preview,
                                          @Param("export_template") String export,
                                          @Param("new_version_template_composition") String newVersionTemplateComposition,
                                          @Param("rollback_template_composition") String rollBackComposition);

    @Query(value = SELECT_FIELDS_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            countQuery = SELECT_COUNT_CLAUSE + SUBQUERY_CLAUSE + FILTER_CONDITION + SEARCH_CONDITION,
            nativeQuery = true)
    Page<TemplatePermissionsModel> search(Pageable pageable,
                                          @Param("name") String templateName,
                                          @Param("role_name") List<String> roleName,
                                          @Param("edit_template") String editTemplate,
                                          @Param("new_version_template_standard") String newVersionTemplateStandard,
                                          @Param("rollback_template_standard") String rollBackStandard,
                                          @Param("preview_template") String preview,
                                          @Param("export_template") String export,
                                          @Param("new_version_template_composition") String newVersionTemplateComposition,
                                          @Param("rollback_template_composition") String rollBackComposition,
                                          @Param("search") String searchParam);


}
