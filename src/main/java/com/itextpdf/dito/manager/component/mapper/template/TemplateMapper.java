package com.itextpdf.dito.manager.component.mapper.template;

import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.entity.TemplateEntity;

import java.util.List;
import org.springframework.data.domain.Page;

public interface TemplateMapper {
    TemplateDTO map(TemplateEntity entity);

    List<TemplateDTO> map(List<TemplateEntity> entities);

    Page<TemplateDTO> map(Page<TemplateEntity> entities);
}
