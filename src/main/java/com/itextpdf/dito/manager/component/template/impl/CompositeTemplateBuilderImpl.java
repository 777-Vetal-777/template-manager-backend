package com.itextpdf.dito.manager.component.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.template.CompositeTemplateBuilder;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.model.template.part.VisibleOnSettings;
import com.itextpdf.dito.manager.service.template.TemplateLoader;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Component
public class CompositeTemplateBuilderImpl implements CompositeTemplateBuilder {
    private static final String DATA_DITO_ELEMENT = "data-dito-element";
    private static final String DATA_DITO_VERTICAL_ALIGN = "data-dito-page-margin-vertical-align";
    private static final String DATA_DITO_PAGE_SELECTOR = "data-dito-header-footer-page-selector";

    private static final Logger LOG = LogManager.getLogger(CompositeTemplateBuilderImpl.class);

    private final TemplateLoader templateLoader;
    private final ObjectMapper objectMapper;

    private static final Map<VisibleOnSettings, Consumer<Element>> VISIBLE_ON_ACTIONS = Map.of(
            VisibleOnSettings.DEFAULT, element -> {},
            VisibleOnSettings.FIRST_PAGE, element -> element.attr(DATA_DITO_PAGE_SELECTOR, "first")
    );

    private final Map<TemplateTypeEnum, BiFunction<Element, TemplateFilePartEntity, Element>> methods = Map.of(
            TemplateTypeEnum.FOOTER, this::addFooter,
            TemplateTypeEnum.HEADER, this::addHeader,
            TemplateTypeEnum.STANDARD, this::addChildObject
    );

    public CompositeTemplateBuilderImpl(final TemplateLoader templateLoader,
                                        final ObjectMapper objectMapper) {
        this.templateLoader = templateLoader;
        this.objectMapper = objectMapper;
    }

    private Element addChildObject(final Element parent, final TemplateFilePartEntity templateFilePartEntity) {
        final String encodedChildName = templateFilePartEntity.getPart().getTemplate().getUuid();

        final Element child = new Element(Tag.valueOf("object"), parent.baseUri());

        child.attr(DATA_DITO_ELEMENT, "fragment");
        child.attr("data-dito-fragment", new StringBuilder("dito-asset://").append(encodedChildName).toString());
        processSettings(child, templateFilePartEntity);

        return parent.appendChild(child);
    }

    private void processSettings(final Element child, final TemplateFilePartEntity templateFilePartEntity) {
        final PartSettings settings = parseSettings(templateFilePartEntity);
        if (settings != null && Boolean.TRUE.equals(settings.getStartOnNewPage())) {
                child.attr("style", "page-break-before:always");
        }

        final VisibleOnSettings visibleOn = Optional.ofNullable(settings).map(PartSettings::getVisibleOn).orElse(VisibleOnSettings.DEFAULT);
        VISIBLE_ON_ACTIONS.get(visibleOn).accept(child);
    }

    private Element addHeader(final Element parent, final TemplateFilePartEntity templateFilePartEntity) {
        final Element child = new Element(Tag.valueOf("header"), parent.baseUri());
        child.attr(DATA_DITO_ELEMENT, "page-header");
        child.attr(DATA_DITO_VERTICAL_ALIGN, "middle");

        return parent.appendChild(addChildObject(child, templateFilePartEntity));
    }

    private Element addFooter(final Element parent, final TemplateFilePartEntity templateFilePartEntity) {
        final Element child = new Element(Tag.valueOf("footer"), parent.baseUri());
        child.attr(DATA_DITO_ELEMENT, "page-footer");
        child.attr(DATA_DITO_VERTICAL_ALIGN, "middle");

        return parent.appendChild(addChildObject(child, templateFilePartEntity));
    }

    private PartSettings parseSettings(final TemplateFilePartEntity part) {
        try {
            return objectMapper.readValue(part.getSettings(), PartSettings.class);
        } catch (JsonProcessingException e) {
            //settings field was broken for some reasons, nothing will be processed
            LOG.warn("Error reading settings from JSON string {}", part.getSettings());
            return null;
        }
    }

    @Override
    public byte[] build(final TemplateFileEntity entity) {
        return build(entity.getParts());
    }

    @Override
    public byte[] build(final List<TemplateFilePartEntity> entities) {
        final Document templateData = Jsoup.parse(new String(templateLoader.load(), StandardCharsets.UTF_8));

        if (entities != null) {
            for (TemplateFilePartEntity part : entities) {
                final TemplateEntity templateEntity = part.getPart().getTemplate();
                methods.get(templateEntity.getType()).apply(templateData.body(), part);
            }
        }

        return templateData.outerHtml().getBytes();
    }
}
