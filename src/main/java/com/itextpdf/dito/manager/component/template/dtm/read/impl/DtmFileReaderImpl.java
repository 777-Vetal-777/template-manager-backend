package com.itextpdf.dito.manager.component.template.dtm.read.impl;

import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileItemReader;
import com.itextpdf.dito.manager.component.template.dtm.read.DtmFileReader;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileImportContext;
import com.itextpdf.dito.manager.model.template.dtm.template.DtmTemplateDescriptorModel;
import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class DtmFileReaderImpl implements DtmFileReader {
    private final Map<Integer, List<DtmFileItemReader>> fileItemReaders;
    private final TemplateRepository templateRepository;

    public DtmFileReaderImpl(final List<DtmFileItemReader> fileItemReaders,
                             final TemplateRepository templateRepository) {
        this.fileItemReaders = fileItemReaders.stream().collect(Collectors.groupingBy(DtmFileItemReader::getPriority, TreeMap::new, Collectors.toList()));
        this.templateRepository = templateRepository;
    }

    @Override
    public List<TemplateEntity> read(final DtmFileImportContext context,
                                     final DtmFileDescriptorModel model,
                                     final Path basePath) {
        fileItemReaders.forEach((priority, dtmFileItemReaders) -> {
                    dtmFileItemReaders.forEach(dtmFileItemReader -> dtmFileItemReader.read(context, model, basePath));
                }
        );
        final List<Long> templateIds = model.getTemplates().stream().map(DtmTemplateDescriptorModel::getId).map(context::getTemplateMapping).collect(Collectors.toList());

        return templateRepository.findAllById(templateIds);
    }
}
