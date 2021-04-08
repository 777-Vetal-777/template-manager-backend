package com.itextpdf.dito.manager.component.template.impl;

import com.itextpdf.dito.manager.component.template.LinksUpdateComponent;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateUpdatingOutdatedLinkException;
import com.itextpdf.dito.manager.service.template.impl.TemplateFileLinkRenamingContext;
import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.template.TemplateSubTreeProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupDocument;
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupTemplateParser;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LinkUpdateComponentImpl implements LinksUpdateComponent {

    @Override
    public void updateLinksInFiles(List<TemplateFileEntity> templateFiles, String oldId, String newId, TemplateSubTreeProcessor<Node, TemplateFileLinkRenamingContext> processor) {
        templateFiles.forEach(file -> {
            try {
                final JsoupDocument template = JsoupTemplateParser.parse(new ByteArrayInputStream(file.getData()), null, "");
                final MutableItemProcessingResult result = processor.process(template, new TemplateFileLinkRenamingContext(oldId, newId));
                if (result.isModified()) {
                    file.setData(template.outerHtml().getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception exception) {
                throw new TemplateUpdatingOutdatedLinkException();
            }
        });
    }
}
