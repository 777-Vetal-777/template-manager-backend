package com.itextpdf.dito.manager.service.template.impl;

import com.itextpdf.dito.sdk.core.process.ImmutableItemProcessingResult;
import com.itextpdf.dito.sdk.core.process.ProjectImmutableItemProcessor;
import com.itextpdf.dito.sdk.core.process.context.ReportLoggingContextImpl;


public class TemplateFileLinkRenamingContext extends ReportLoggingContextImpl {
    private final String from;
    private final String to;

    public ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> getRenamingUrlProcessor() {
        return renamingUrlProcessor;
    }

    private final ProjectImmutableItemProcessor<String, TemplateFileLinkRenamingContext> renamingUrlProcessor = (item, renamingContext) -> {
        if (renamingContext.getFrom().equals(item)) {
            return ImmutableItemProcessingResult.modified(renamingContext.getTo()).build();
        }
        return ImmutableItemProcessingResult.unmodified(item).build();
    };


    public TemplateFileLinkRenamingContext(final String from, final String to) {
        this.from = "dito-asset://".concat(from);
        this.to = "dito-asset://".concat(to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }




}

