package com.itextpdf.dito.manager.service.template.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileReader;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.template.TemplateImportHasDuplicateNamesException;
import com.itextpdf.dito.manager.exception.template.TemplateImportProjectException;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.service.template.TemplateDtmImportService;
import com.itextpdf.dito.manager.util.FilesUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class TemplateDtmImportServiceImpl implements TemplateDtmImportService {
    private final ObjectMapper objectMapper;
    private final DtmFileReader dtmFileReader;

    public TemplateDtmImportServiceImpl(final ObjectMapper objectMapper,
                                        final DtmFileReader dtmFileReader) {
        this.objectMapper = objectMapper;
        this.dtmFileReader = dtmFileReader;
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public List<TemplateEntity> importTemplate(final String fileName,
                                               final byte[] data,
                                               final String email,
                                               final Map<SettingType, Map<String, TemplateImportNameModel>> settings) {
        final Path tempFolder;
        try {
            tempFolder = Files.createTempDirectory(FilesUtils.TEMP_DIRECTORY.toPath(), fileName);
            FilesUtils.unZip(tempFolder, data);
        } catch (IOException e) {
            throw new TemplateImportProjectException(e);
        }

        final Path metaDataPath = Path.of(tempFolder.toString(), "meta.json");
        final DtmFileDescriptorModel metaData;
        try {
            metaData = objectMapper.readValue(metaDataPath.toFile(), DtmFileDescriptorModel.class);
            if (!"DTM2".equalsIgnoreCase(metaData.getFormat())) {
                throw new IOException("Template format is not supported");
            }
        } catch (IOException e) {
            FileUtils.deleteQuietly(tempFolder.toFile());
            throw new TemplateImportProjectException(e);
        }

        final DtmFileImportContext context = new DtmFileImportContext(settings, email, fileName);

        final List<TemplateEntity> templateEntities;
        try {
            templateEntities = dtmFileReader.read(context, metaData, tempFolder);
        } finally {
            FileUtils.deleteQuietly(tempFolder.toFile());
        }

        if (!context.isDuplicatesEmpty()) {
            throw new TemplateImportHasDuplicateNamesException("Template file got duplicates", context.getDuplicatesList());
        }

        return templateEntities;
    }
}
