package com.itextpdf.dito.manager.component.mapper.template;

import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateWithSettingsDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.dto.template.deployment.TemplateDescriptorDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.version.TemplateDeployedVersionDTO;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.TemplatePartModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TemplateMapper {
    TemplateDTO map(TemplateEntity entity, String email);

    TemplateEntity map(TemplateUpdateRequestDTO dto);

    TemplateMetadataDTO mapToMetadata(TemplateEntity entity, String email);

    TemplateDescriptorDTO mapToDescriptor(TemplateFileEntity templateFileEntity, boolean versionAliasRequired);

    List<TemplateDTO> map(List<TemplateEntity> entities, String email);

    List<TemplateWithSettingsDTO> mapTemplatesWithPart(List<TemplateEntity> entities, String email);

    TemplateWithSettingsDTO mapTemplateWithPart(TemplateEntity entity, String email);

    TemplateDeployedVersionDTO map(TemplateFileEntity templateFileEntity);

    List<TemplateDeployedVersionDTO> mapToDeployedVersions(TemplateEntity templateEntity);

    TemplatePartModel mapPartDto(TemplatePartDTO dto);

    List<TemplatePartModel> mapPartDto(List<TemplatePartDTO> dto);

    PartSettings mapPartSettings(TemplateFilePartEntity entity);

    Page<TemplateDTO> mapModels(Page<TemplateModelWithRoles> models, String email);

    TemplateDTO mapModel(TemplateModelWithRoles model, String email);

}
