package com.itextpdf.dito.manager.service.template;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TemplateService {
    TemplateEntity create(String templateName, TemplateTypeEnum templateTypeEnum, String dataCollectionName, String email);

    Page<TemplateEntity> getAll(Pageable pageable, TemplateFilter templateFilter, String searchParam);

    List<TemplateEntity> getAll();

    TemplateEntity get(String name);

    TemplateEntity update(String name, TemplateEntity updatedTemplateEntity, String userEmail);

    TemplateEntity createNewVersion(String name, byte[] data, String email, String comment);

    TemplateEntity createNewVersion(String name, byte[] data, String email, String comment, String templateName);

    TemplateEntity createNewVersionAsCopy(TemplateFileEntity fileEntityToCopy, UserEntity userEntity, String comment);

    Page<RoleEntity> getRoles(Pageable pageable, String name, TemplatePermissionFilter filter);

    TemplateEntity applyRole(String templateName, String roleName, List<String> permissions, String email);

    TemplateEntity detachRole(String templateName, String roleName, String email);

    TemplateEntity delete(String templateName);

    TemplateEntity block(String userEmail, String templateName);

    TemplateEntity unblock(String userEmail, String templateName);
}
