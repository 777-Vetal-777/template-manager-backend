package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface TemplateService {
    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email, List<TemplatePartModel> templateParts);

    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email);

    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum,
                          String dataCollectionName, String email, byte[] data,
                          List<TemplatePartModel> templateParts);

    Page<TemplateModelWithRoles> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam);

    List<TemplateEntity> getAll();

    List<TemplateEntity> getAll(List<TemplateTypeEnum> filter);

    List<TemplateEntity> getAll(TemplateListFilter templateListFilter);

    List<TemplateEntity> getAllParts(String templateName);

    TemplateEntity get(String name);

    TemplateEntity getByUuid(String uuid);

    TemplateEntity update(String name, TemplateEntity updatedTemplateEntity, String userEmail);

    @PreAuthorize("@permissionHandlerImpl.checkTemplateCreateVersionPermission(authentication, #name)")
    TemplateEntity createNewVersion(String name, byte[] data, String email, String comment, String templateName, List<TemplatePartModel> templateParts);

    @PreAuthorize("@permissionHandlerImpl.checkTemplateCreateVersionPermission(#userEntity, #fileEntityToCopy.getTemplate())")
    TemplateEntity createNewVersionAsCopy(TemplateFileEntity fileEntityToCopy, UserEntity userEntity, String comment);

    TemplateEntity rollbackTemplate(TemplateEntity existingTemplateEntity, TemplateFileEntity templateVersionToBeRevertedTo, UserEntity userEntity);

    TemplateEntity applyRole(String templateName, String roleName, List<String> permissions, String email);

    TemplateEntity detachRole(String templateName, String roleName, String email);

    TemplateEntity delete(String templateName);

    TemplateEntity block(String userEmail, String templateName);

    TemplateEntity unblock(String userEmail, String templateName);
}
