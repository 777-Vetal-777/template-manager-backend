package com.itextpdf.dito.manager.component.template;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;

import java.util.List;

public interface CompositeTemplateBuilder {

    byte[] build(TemplateFileEntity entity);

    byte[] build(List<TemplateFilePartEntity> entities);


}
