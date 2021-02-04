package com.itextpdf.dito.manager.component.template.impl;

import com.itextpdf.dito.manager.component.template.CompositeTemplateBuilder;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
public class CompositeTemplateBuilderImpl implements CompositeTemplateBuilder {

    private static final String DATA_DITO_ELEMENT = "data-dito-element";
    private static final String DATA_DITO_VERTICAL_ALIGN = "data-dito-page-margin-vertical-align";
    final TemplateLoader templateLoader;

    private final Map<TemplateTypeEnum, BiFunction<Element, String, Element>> methods = Map.of(
            TemplateTypeEnum.FOOTER, this::addFooter,
            TemplateTypeEnum.HEADER, this::addHeader,
            TemplateTypeEnum.STANDARD, this::addChildObject
            );

    public CompositeTemplateBuilderImpl(final TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    private String encodeToBase64(final String value) {
        return new String(Base64.getUrlEncoder().encode(value.getBytes()));
    }

    private Element addChildObject(final Element parent, final String childName) {
        final String encodedChildName = encodeToBase64(childName);

        final Element child = new Element(Tag.valueOf("object"), parent.baseUri());

        child.attr(DATA_DITO_ELEMENT, "fragment");
        child.attr("data-dito-fragment", new StringBuilder("dito-asset://").append(encodedChildName).toString());

        return parent.appendChild(child);
    }

    private Element addHeader(final Element parent, final String childName) {
        final Element child = new Element(Tag.valueOf("header"), parent.baseUri());
        child.attr(DATA_DITO_ELEMENT, "page-header");
        child.attr(DATA_DITO_VERTICAL_ALIGN, "middle");

        return parent.appendChild(addChildObject(child, childName));
    }

    private Element addFooter(final Element parent, final String childName) {
        final Element child = new Element(Tag.valueOf("footer"), parent.baseUri());
        child.attr(DATA_DITO_ELEMENT, "page-footer");
        child.attr(DATA_DITO_VERTICAL_ALIGN, "middle");

        return parent.appendChild(addChildObject(child, childName));
    }

    @Override
    public byte[] build(final TemplateFileEntity entity) {
        final Document templateData = Jsoup.parse(new String(templateLoader.load()));

        final List<TemplateFilePartEntity> parts = entity.getParts();
        if (parts != null) {
            for (TemplateFilePartEntity part : parts) {
                final TemplateEntity templateEntity = part.getPart().getTemplate();
                methods.get(templateEntity.getType())
                        .apply(templateData.body(), templateEntity.getName());
            }
        }

        return templateData.outerHtml().getBytes();
    }

}
