package com.itextpdf.dito.manager.service.template.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFilePartEntity;
import com.itextpdf.dito.manager.exception.template.TemplateHasWrongStructureException;
import com.itextpdf.dito.manager.exception.template.TemplateNotFoundException;
import com.itextpdf.dito.manager.model.template.part.PartSettings;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import com.itextpdf.dito.manager.service.template.TemplateFilePartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TemplateFilePartServiceImpl implements TemplateFilePartService {

    private static final Logger LOG = LogManager.getLogger(TemplateFilePartServiceImpl.class);

    private final ObjectMapper objectMapper;

    private final TemplateRepository templateRepository;

    public TemplateFilePartServiceImpl(final TemplateRepository templateRepository,
                                       final ObjectMapper objectMapper) {
        this.templateRepository = templateRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<TemplateFilePartEntity> createTemplatePartEntities(final String dataCollectionName,
                                                                   final List<TemplatePartDTO> templatePartDTOs) {
        final List<TemplateEntity> templatePartList = templateRepository.getTemplatesWithLatestFileByName(templatePartDTOs.stream().map(TemplatePartDTO::getName).collect(Collectors.toList()));
        final Map<String, TemplateFileEntity> templateFilePartMap = templatePartList.stream().collect(Collectors.toMap(TemplateEntity::getName, TemplateEntity::getLatestFile, (templateFileEntity1, templateFileEntity2) -> templateFileEntity1));

        //checks that all templates exists
        throwExceptionIfSomeTemplatesAreNotFound(templatePartDTOs, templateFilePartMap);

        //checks that all templates has the same (or absent dataCollection)
        throwExceptionIfTemplatesHaveAnotherDataCollections(templatePartList, dataCollectionName);

        //checks that exists at most one HEADER and FOOTER
        throwExceptionIfPartsSizeAreIncorrect(templatePartList);

        final List<TemplateFilePartEntity> parts = templatePartDTOs.stream().map(templatePart -> {
            final TemplateFileEntity partTemplateFileEntity = templateFilePartMap.get(templatePart.getName());
            final TemplateFilePartEntity templateFilePartEntity = createTemplateFilePartEntity(partTemplateFileEntity, templatePart);
            return templateFilePartEntity;
        }).collect(Collectors.toList());

        return parts;
    }

    private TemplateFilePartEntity createTemplateFilePartEntity(final TemplateFileEntity partTemplateFileEntity,
                                                                final TemplatePartDTO templatePart) {
        return createTemplateFilePartEntity(null, partTemplateFileEntity, templatePart.getCondition(), getSettingsString(templatePart));
    }

    private String getSettingsString(final TemplatePartDTO templatePart) {
        final PartSettings partSettings = new PartSettings();
        Optional.ofNullable(templatePart.getStartOnNewPage()).ifPresent(partSettings::setStartOnNewPage);
        try {
            return objectMapper.writeValueAsString(partSettings);
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return null;
    }

    @Override
    public TemplateFilePartEntity updateComposition(final TemplateFilePartEntity example,
                                                    final TemplateFileEntity composition) {
        return createTemplateFilePartEntity(composition, example.getPart(), example.getCondition(), example.getSettings());
    }

    @Override
    public TemplateFilePartEntity updatePart(final TemplateFilePartEntity example,
                                             final TemplateFileEntity part) {
        return createTemplateFilePartEntity(example.getComposition(), part, example.getCondition(), example.getSettings());
    }

    private TemplateFilePartEntity createTemplateFilePartEntity(final TemplateFileEntity compositionTemplateFileEntity,
                                                                final TemplateFileEntity partTemplateFileEntity,
                                                                final String condition,
                                                                final String settings) {
        final TemplateFilePartEntity templateFilePartEntity = new TemplateFilePartEntity();
        templateFilePartEntity.setPart(partTemplateFileEntity);
        templateFilePartEntity.setComposition(compositionTemplateFileEntity);
        templateFilePartEntity.setCondition(condition);
        templateFilePartEntity.setSettings(settings);
        return templateFilePartEntity;
    }

    private void throwExceptionIfPartsSizeAreIncorrect(final List<TemplateEntity> templatePartList) {
        final Map<TemplateTypeEnum, Integer> mapOfPartsCount = templatePartList.stream().collect(
                Collectors.groupingBy(TemplateEntity::getType,
                        Collectors.collectingAndThen(Collectors.toList(), List::size)));
        throwExceptionIfHaveCompositeParts(mapOfPartsCount);
        throwExceptionIfTooManyForType(mapOfPartsCount, TemplateTypeEnum.HEADER);
        throwExceptionIfTooManyForType(mapOfPartsCount, TemplateTypeEnum.FOOTER);
        throwExceptionIfTooFewForType(mapOfPartsCount, TemplateTypeEnum.STANDARD);
    }

    private void throwExceptionIfTooManyForType(final Map<TemplateTypeEnum, Integer> mapOfPartsCount, final TemplateTypeEnum checkedType) {
        if (mapOfPartsCount.getOrDefault(checkedType, 0) > 1) {
            throw new TemplateHasWrongStructureException(new StringBuilder("Template parts have more than one ").append(checkedType).toString());
        }
    }

    private void throwExceptionIfHaveCompositeParts(final Map<TemplateTypeEnum, Integer> mapOfPartsCount) {
        if (mapOfPartsCount.containsKey(TemplateTypeEnum.COMPOSITION)) {
            throw new TemplateHasWrongStructureException("Can not include composition template to another one");
        }
    }

    private void throwExceptionIfTooFewForType(final Map<TemplateTypeEnum, Integer> mapOfPartsCount, final TemplateTypeEnum checkedType) {
        if (mapOfPartsCount.getOrDefault(checkedType, 0) < 1) {
            throw new TemplateHasWrongStructureException(new StringBuilder("Template parts have too few of ").append(checkedType).append(" templates").toString());
        }
    }

    private void throwExceptionIfTemplatesHaveAnotherDataCollections(final List<TemplateEntity> templatePartList, final String dataCollectionName) {
        if (!templatePartList.stream().allMatch(entity -> {
            final String dataCollection = Optional.ofNullable(entity.getLatestFile().getDataCollectionFile()).map(DataCollectionFileEntity::getDataCollection).map(DataCollectionEntity::getName).orElse(null);
            return Objects.equals(dataCollectionName, dataCollection) || Objects.equals(null, dataCollection);
        })) {
            throw new TemplateHasWrongStructureException("Template parts belong to different Data Collections");
        }
    }

    private void throwExceptionIfSomeTemplatesAreNotFound(final List<TemplatePartDTO> templatePartDTOs, final Map<String, TemplateFileEntity> templateFilePartMap) {
        final List<String> missedTemplateNames = templatePartDTOs.stream().map(TemplatePartDTO::getName).filter(partName -> !templateFilePartMap.containsKey(partName)).collect(Collectors.toList());
        if (!missedTemplateNames.isEmpty()) {
            throw new TemplateNotFoundException(missedTemplateNames.get(0));
        }
    }

}
