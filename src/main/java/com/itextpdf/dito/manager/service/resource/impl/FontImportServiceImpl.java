package com.itextpdf.dito.manager.service.resource.impl;

import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.model.template.duplicates.DuplicatesList;
import com.itextpdf.dito.manager.service.resource.EmbeddedResourceImportService;
import com.itextpdf.dito.manager.util.templateimport.StyleTagRenamingContext;
import com.itextpdf.dito.manager.util.templateimport.font.FontPreprocessorService;
import com.itextpdf.dito.manager.util.templateimport.font.StyleFontProcessor;
import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectMutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.template.TemplateSubTreeProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupDocument;
import com.itextpdf.dito.sdk.internal.core.template.parser.impl.jsoup.JsoupTemplateParser;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Element;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
public class FontImportServiceImpl implements EmbeddedResourceImportService {

    private final ProjectMutableItemProcessor<Element, StyleTagRenamingContext> styleSheetPreprocessorService;

    public FontImportServiceImpl(final @Qualifier(FontPreprocessorService.BEAN_ID) ProjectMutableItemProcessor<Element, StyleTagRenamingContext> styleSheetPreprocessorService) {
        this.styleSheetPreprocessorService = styleSheetPreprocessorService;
    }

    @Override
    public byte[] importEmbedded(final byte[] templateBody, final String fileName,
                                 final Map<String, TemplateImportNameModel> settings,
                                 final DuplicatesList duplicatesList,
                                 final String email) throws IOException {
        final TemplateSubTreeProcessor<Node, StyleTagRenamingContext> processor = new TemplateSubTreeProcessor<>(Collections.singletonList(new StyleFontProcessor<>(styleSheetPreprocessorService)));

        final StyleTagRenamingContext context = new StyleTagRenamingContext(fileName, settings, duplicatesList, email);

        final JsoupDocument template = JsoupTemplateParser.parse(new ByteArrayInputStream(templateBody), null, "");
        final MutableItemProcessingResult result = processor.process(template, context);

        return result.isModified() ? template.outerHtml().getBytes(StandardCharsets.UTF_8) : templateBody;
    }

}
