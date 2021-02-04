package com.itextpdf.dito.manager.component.template;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

public interface CompositeTemplateBuilder {

    byte[] build(TemplateFileEntity entity);

}
