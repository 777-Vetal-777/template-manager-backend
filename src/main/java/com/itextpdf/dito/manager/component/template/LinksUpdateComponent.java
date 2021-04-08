package com.itextpdf.dito.manager.component.template;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.service.template.impl.TemplateFileLinkRenamingContext;
import com.itextpdf.dito.sdk.core.process.template.TemplateSubTreeProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;

import java.util.List;

public interface LinksUpdateComponent {

    void updateLinksInFiles(final List<TemplateFileEntity> templateFiles, final String oldId, final String newId, final TemplateSubTreeProcessor<Node, TemplateFileLinkRenamingContext> processor);

}
