package com.itextpdf.dito.manager.component.template.dtm.model.impl;

import com.itextpdf.dito.manager.component.template.dtm.model.DtmItemModelExtractor;
import com.itextpdf.dito.manager.component.template.dtm.model.DtmModelExtractor;
import com.itextpdf.dito.manager.model.template.dtm.DtmFileDescriptorModel;
import com.itextpdf.dito.manager.model.template.dtm.context.DtmFileExportContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class DtmModelExtractorImpl implements DtmModelExtractor {
    private final Map<Integer, List<DtmItemModelExtractor>> itemModelExtractors;

    public DtmModelExtractorImpl(final List<DtmItemModelExtractor> extractors) {
        itemModelExtractors = extractors.stream().collect(Collectors.groupingBy(DtmItemModelExtractor::getPriority, TreeMap::new, Collectors.toList()));
    }

    @Override
    public void extract(final DtmFileExportContext context, final DtmFileDescriptorModel model) {
        itemModelExtractors.forEach(
                (integer, dtmItemModelExtractors) -> {
                    dtmItemModelExtractors.forEach(dtmItemModelExtractor -> dtmItemModelExtractor.extract(context, model));
                }
        );
    }
}
