package com.itextpdf.dito.manager.component.template.dtm.model.impl;

import com.itextpdf.dito.manager.component.mapper.template.dtm.LocalPathMapper;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmItemModelExtractor;
import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.resource.ResourceIdMapper;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmFontFaceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.resource.DtmResourceVersionDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.DtmUsedInDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.ItemType;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.itextpdf.dito.manager.util.TemplateUtils.DITO_ASSET_TAG;

@Component
public class DtmResourceModelExtractorImpl implements DtmItemModelExtractor {

    private final LocalPathMapper localPathMapper;
    private final ResourceIdMapper resourceIdMapper;

    public DtmResourceModelExtractorImpl(final LocalPathMapper localPathMapper,
                                         final ResourceIdMapper resourceIdMapper) {
        this.localPathMapper = localPathMapper;
        this.resourceIdMapper = resourceIdMapper;
    }

    @Override
    public ItemType getType() {
        return ItemType.RESOURCE;
    }

    @Override
    public DtmFileDescriptorModel extract(final DtmFileExportContext context, final DtmFileDescriptorModel model) {
        if (context.isExportDependencies()) {
            final AtomicLong currentId = new AtomicLong(1);

            final Map<ResourceEntity, Map<Long, List<ResourceFileEntity>>> collectedResources = context
                    .getTemplateFileEntities()
                    .stream()
                    .flatMap(templateFileEntity -> templateFileEntity.getResourceFiles().stream())
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(ResourceFileEntity::getId))), ArrayList::new))
                    .stream()
                    .collect(Collectors.groupingBy(ResourceFileEntity::getResource, Collectors.groupingBy(ResourceFileEntity::getVersion, TreeMap::new, Collectors.toList())));
            final List<DtmResourceDescriptorModel> resourceModels = new ArrayList<>();

            collectedResources.forEach((resourceEntity, resourceFilesMap) -> {
                        context.map(resourceEntity, currentId.getAndIncrement());
                        final DtmResourceDescriptorModel descriptorModel = new DtmResourceDescriptorModel();
                        descriptorModel.setId(context.getMapping(resourceEntity).toString());
                        descriptorModel.setName(resourceEntity.getName());
                        descriptorModel.setType(resourceEntity.getType());
                        descriptorModel.setDescription(resourceEntity.getDescription());
                        descriptorModel.setAlias(DITO_ASSET_TAG.concat(resourceIdMapper.mapToId(resourceEntity)));
                        descriptorModel.setVersions(
                                mapVersionDescriptors(resourceEntity.getType(),
                                        resourceFilesMap,
                                        context))
                        ;
                        resourceModels.add(descriptorModel);
                    }
            );

            model.setResources(resourceModels);
        }
        return model;

    }

    private List<DtmResourceVersionDescriptorModel> mapVersionDescriptors(final ResourceTypeEnum type,
                                                                          final Map<Long, List<ResourceFileEntity>> resourceVersions,
                                                                          final DtmFileExportContext context) {
        final List<DtmResourceVersionDescriptorModel> versionDescriptors = new ArrayList<>();
        final AtomicLong currentVersion = new AtomicLong(1);
        resourceVersions.forEach(
                (version, resourceFileEntities) -> {
                    final DtmResourceVersionDescriptorModel resourceVersionModel = new DtmResourceVersionDescriptorModel();
                    final ResourceFileEntity fileEntity = resourceFileEntities.get(0);
                    context.map(fileEntity, currentVersion.getAndIncrement());
                    resourceVersionModel.setVersion(currentVersion.get());
                    resourceVersionModel.setComment(fileEntity.getComment());
                    resourceVersionModel.setUsedIn(
                            fileEntity.getTemplateFiles().stream().flatMap(templateFileEntity -> {
                                final Stream<DtmUsedInDescriptorModel> result;
                                if (context.getMapping(templateFileEntity) != null) {
                                    final DtmUsedInDescriptorModel usedIn = new DtmUsedInDescriptorModel();
                                    usedIn.setType(ItemType.TEMPLATE.getPluralName());
                                    usedIn.setId(context.getMapping(templateFileEntity.getTemplate()));
                                    usedIn.setVersion(context.getMapping(templateFileEntity));
                                    result = Stream.of(usedIn);
                                } else {
                                    result = Stream.empty();
                                }
                                return result;
                            }).collect(Collectors.toList())
                    );
                    if (ResourceTypeEnum.FONT.equals(type)) {
                        resourceVersionModel.setFontFaces(resourceFileEntities.stream().collect(Collectors.toMap(ResourceFileEntity::getFontName, resourceFileEntity -> {
                            final DtmFontFaceDescriptorModel fontFace = new DtmFontFaceDescriptorModel();
                            fontFace.setFileName(resourceFileEntity.getFileName());
                            fontFace.setLocalPath(localPathMapper.getLocalPath(resourceFileEntity));
                            fontFace.setAlias(DITO_ASSET_TAG.concat(resourceFileEntity.getUuid()));
                            return fontFace;
                        })));
                    } else {
                        resourceVersionModel.setFileName(fileEntity.getFileName());
                        resourceVersionModel.setLocalPath(localPathMapper.getLocalPath(fileEntity));
                        resourceVersionModel.setAlias(DITO_ASSET_TAG.concat(fileEntity.getUuid()));
                    }
                    versionDescriptors.add(resourceVersionModel);
                }
        );

        return versionDescriptors;
    }
}
