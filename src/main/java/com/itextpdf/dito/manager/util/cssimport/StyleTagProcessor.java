package com.itextpdf.dito.manager.util.cssimport;

import com.itextpdf.dito.sdk.core.process.MutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectMutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.template.node.NodeMutableProcessor;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Element;
import com.itextpdf.dito.sdk.internal.core.template.parser.nodes.Node;
import com.itextpdf.html2pdf.html.TagConstants;
import org.springframework.util.StringUtils;

import static com.itextpdf.html2pdf.html.AttributeConstants.HREF;

public class StyleTagProcessor<C> implements NodeMutableProcessor<C> {

    private final ProjectMutableItemProcessor<Element, C> cssStyleSheetPreprocessor;

    public StyleTagProcessor(
            ProjectMutableItemProcessor<Element, C> cssStyleSheetPreprocessor) {
        this.cssStyleSheetPreprocessor = cssStyleSheetPreprocessor;
    }

    @Override
    public MutableItemProcessingResult process(Node item, C context) {
        final MutableItemProcessingResult.Builder result = MutableItemProcessingResult.unmodified();
        if (item instanceof Element) {
            Element element = (Element) item;
            if (TagConstants.STYLE.equals(element.tagName())
                    && StringUtils.isEmpty(element.attr(StyleSheetTagConstants.STYLESHEET_INTERNAL_NAME))
                    && (!StringUtils.isEmpty(element.attr(StyleSheetTagConstants.STYLESHEET_NAME)) || !StringUtils.isEmpty(element.attr(HREF)))) {
                result.setModified(cssStyleSheetPreprocessor.process(element, context).isModified());
            }
        }
        return result.build();
    }

    @Override
    public void onProcessingComplete(Node node, C context) {
        //not used here
    }
}
