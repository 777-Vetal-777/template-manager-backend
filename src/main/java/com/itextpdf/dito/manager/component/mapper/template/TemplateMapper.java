package com.itextpdf.dito.manager.component.mapper.template;

import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateVersionDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TemplateMapper {
    TemplateDTO map(TemplateEntity entity);

    TemplateEntity map(TemplateUpdateRequestDTO dto);

    TemplateMetadataDTO mapToMetadata(TemplateEntity entity);

    List<TemplateDTO> map(List<TemplateEntity> entities);

    Page<TemplateDTO> map(Page<TemplateEntity> entities);

    TemplateVersionDTO map(TemplateFileEntity templateEntity);

    Page<TemplateVersionDTO> mapVersions(Page<TemplateFileEntity> templateEntities);
}
