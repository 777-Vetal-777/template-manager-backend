package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TemplateService {
    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email, List<TemplatePartDTO> templateParts);

    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email);

    Page<TemplateEntity> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam);

    List<TemplateEntity> getAll();

    List<TemplateEntity> getAll(TemplateListFilter templateListFilter);

    TemplateEntity get(String name);

    TemplateEntity update(String name, TemplateEntity updatedTemplateEntity, String userEmail);

    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(#email, #name, 'E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD')")
    TemplateEntity createNewVersion(final String name, final byte[] data, final String email, final String comment, final String templateName);

    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(#userEntity, #fileEntityToCopy.getTemplate(), 'E9_US76_CREATE_NEW_VERSION_OF_TEMPLATE_STANDARD')")
    TemplateEntity createNewVersionAsCopy(TemplateFileEntity fileEntityToCopy, UserEntity userEntity, String comment);

    @PreAuthorize("@permissionHandlerImpl.checkTemplatePermissions(#userEntity, #existingTemplateEntity, 'E9_US80_ROLLBACK_OF_THE_STANDARD_TEMPLATE')")
    TemplateEntity rollbackTemplate(TemplateEntity existingTemplateEntity, TemplateFileEntity templateVersionToBeRevertedTo, UserEntity userEntity);

    Page<RoleEntity> getRoles(Pageable pageable, String name, TemplatePermissionFilter filter);

    TemplateEntity applyRole(String templateName, String roleName, List<String> permissions, String email);

    TemplateEntity detachRole(String templateName, String roleName, String email);

    TemplateEntity delete(String templateName);

    TemplateEntity block(String userEmail, String templateName);

    TemplateEntity unblock(String userEmail, String templateName);
}
