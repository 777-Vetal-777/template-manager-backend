package com.itextpdf.dito.manager.component.template.dtm.model.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmItemModelExtractor;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.template.TemplateIdMapper;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Component
public class DtmTemplateModelExtractorImpl implements DtmItemModelExtractor {

    private final LocalPathMapper localPathMapper;
    private final TemplateIdMapper templateIdMapper;

    public DtmTemplateModelExtractorImpl(final LocalPathMapper localPathMapper,
                                         final TemplateIdMapper templateIdMapper) {
        this.localPathMapper = localPathMapper;
        this.templateIdMapper = templateIdMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.TEMPLATE;
    }

    @Override
    public DtmFileDescriptorModel extract(final DtmFileExportContext context, final DtmFileDescriptorModel model) {
        if (context.isExportDependencies()) {
            updateContext(context);
        }
        final AtomicLong currentId = new AtomicLong(1);

        final Map<TemplateEntity, List<TemplateFileEntity>> collectedTemplates = context.getTemplateFileEntities().stream().collect(Collectors.groupingBy(TemplateFileEntity::getTemplate));
        final List<DtmTemplateDescriptorModel> templateModels = new ArrayList<>();

        collectedTemplates.keySet().stream().sorted(Comparator.comparing(TemplateEntity::getType).reversed()).forEachOrdered(templateEntity -> {
            context.map(templateEntity, currentId.getAndIncrement());
            final DtmTemplateDescriptorModel descriptorModel = new DtmTemplateDescriptorModel();
            descriptorModel.setId(context.getMapping(templateEntity).toString());
            descriptorModel.setName(templateEntity.getName());
            descriptorModel.setType(templateEntity.getType());
            descriptorModel.setDescription(templateEntity.getDescription());
            descriptorModel.setAlias(DITO_ASSET_TAG.concat(templateIdMapper.mapToId(templateEntity)));
            descriptorModel.setVersions(mapVersionDescriptors(collectedTemplates.get(templateEntity), context));
            templateModels.add(descriptorModel);
        });

        model.setTemplates(templateModels);

        return model;
    }

    private void updateContext(final DtmFileExportContext context) {
        final Collection<TemplateFileEntity> updatedTemplates =
                context.getTemplateFileEntities().stream()
                        .map(TemplateFileEntity::getParts)
                        .flatMap(List::stream)
                        .map(TemplateFilePartEntity::getPart)
                        .collect(Collectors.toSet());

        context.addAll(updatedTemplates);
    }

    private List<DtmTemplateVersionDescriptorModel> mapVersionDescriptors(final List<TemplateFileEntity> templates, final DtmFileExportContext context) {
        final List<DtmTemplateVersionDescriptorModel> versionDescriptors = new ArrayList<>();
        final AtomicLong currentVersion = new AtomicLong(1);
        templates.stream().sorted(Comparator.comparing(TemplateFileEntity::getVersion)).forEachOrdered(
                templateFileEntity -> {
                    final DtmTemplateVersionDescriptorModel templateModel = new DtmTemplateVersionDescriptorModel();
                    context.map(templateFileEntity, currentVersion.getAndIncrement());
                    templateModel.setVersion(templateFileEntity.getVersion());
                    templateModel.setComment(templateFileEntity.getComment());
                    templateModel.setAlias(DITO_ASSET_TAG.concat(templateFileEntity.getUuid()));
                    templateModel.setLocalPath(localPathMapper.getLocalPath(templateFileEntity));
                    templateModel.setUsedIn(
                            templateFileEntity.getCompositions().stream().flatMap(templateFilePartEntity -> {
                                final Stream<DtmTemplateUsedInDescriptorModel> result;
                                if (context.getMapping(templateFilePartEntity.getComposition()) != null) {
                                    final DtmTemplateUsedInDescriptorModel usedIn = new DtmTemplateUsedInDescriptorModel();
                                    usedIn.setType(ItemType.TEMPLATE.getPluralName());
                                    usedIn.setId(context.getMapping(templateFilePartEntity.getComposition().getTemplate()));
                                    usedIn.setVersion(context.getMapping(templateFilePartEntity.getComposition()));
                                    usedIn.setConditions(templateFilePartEntity.getCondition());
                                    usedIn.setSettings(templateFilePartEntity.getSettings());
                                    result = Stream.of(usedIn);
                                } else {
                                    result = Stream.empty();
                                }
                                return result;
                            }).collect(Collectors.toList())
                    );
                    versionDescriptors.add(templateModel);
                }
        );
        return versionDescriptors;
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
