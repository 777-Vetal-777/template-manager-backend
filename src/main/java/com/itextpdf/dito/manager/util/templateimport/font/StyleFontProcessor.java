package com.itextpdf.dito.manager.util.templateimport.font;

import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectMutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.NodeMutableProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Element;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import com.itextpdf.html2pdf.html.TagConstants;
import org.springframework.util.StringUtils;

import static com.itextpdf.dito.manager.util.templateimport.font.FontTagConstants.FONT_RESOURCE;
import static com.itextpdf.dito.manager.util.templateimport.font.FontTagConstants.FONT_RESOURCE_URI;
import static com.itextpdf.dito.manager.util.templateimport.font.FontTagConstants.STYLE_INTERNAL_NAME;

public class StyleFontProcessor<C> implements NodeMutableProcessor<C> {

    private final ProjectMutableItemProcessor<Element, C> styleSheetPreprocessor;

    public StyleFontProcessor(final ProjectMutableItemProcessor<Element, C> styleSheetPreprocessor) {
        this.styleSheetPreprocessor = styleSheetPreprocessor;
    }

    @Override
    public MutableItemProcessingResult process(Node item, C context) {
        final MutableItemProcessingResult.Builder result = MutableItemProcessingResult.unmodified();
        if (item instanceof Element) {
            Element element = (Element) item;
            if (TagConstants.STYLE.equals(element.tagName())
                    && FONT_RESOURCE.equals(element.attr(STYLE_INTERNAL_NAME))
                    && !StringUtils.isEmpty(element.attr(FONT_RESOURCE_URI))) {
                result.setModified(styleSheetPreprocessor.process(element, context).isModified());
            }
        }
        return result.build();
    }

    @Override
    public void onProcessingComplete(Node node, C context) {
        //not used here
    }
}
