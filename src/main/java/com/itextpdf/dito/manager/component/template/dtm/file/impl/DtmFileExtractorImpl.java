package com.itextpdf.dito.manager.component.template.dtm.file.impl;

import com.itextpdf.dito.manager.component.template.dtm.file.DtmFileExtractor;
import com.itextpdf.dito.manager.component.template.dtm.file.DtmItemFileExtractor;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
public class DtmFileExtractorImpl implements DtmFileExtractor {

    private final List<DtmItemFileExtractor> extractors;

    public DtmFileExtractorImpl(final List<DtmItemFileExtractor> extractors) {
        this.extractors = extractors;
    }

    @Override
    public void extractFiles(final Path directory, final DtmFileExportContext context) throws IOException {
        for (DtmItemFileExtractor extractor : extractors) {
            extractor.extract(directory, context);
        }
    }
}
