package com.itextpdf.dito.manager.util.fontimport;

import com.itextpdf.dito.manager.util.cssimport.StyleTagRenamingContext;
import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectMutableItemProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Element;
import org.springframework.stereotype.Service;

import static com.itextpdf.dito.manager.util.fontimport.FontTagConstants.FONT_RESOURCE_URI;

@Service(FontPreprocessorService.BEAN_ID)
public class FontPreprocessorService implements ProjectMutableItemProcessor<Element, StyleTagRenamingContext> {
    public static final String BEAN_ID = "FontPreprocessorService";

    @Override
    public MutableItemProcessingResult process(final Element element, final StyleTagRenamingContext context) {
        final MutableItemProcessingResult.Builder result = MutableItemProcessingResult.modified();

        element.attr(FONT_RESOURCE_URI, null);
        element.html("");

        return result.build();
    }

}
