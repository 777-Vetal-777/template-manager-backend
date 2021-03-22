package com.itextpdf.dito.manager.component.retriever;

import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.sdk.core.preprocess.asset.TemplateAssetRetriever;

public interface RetrieverBuilder {
    TemplateAssetRetriever buildTemplateAssetRetriever(TemplateFileEntity templateFileEntity);

    TemplateAssetRetriever buildResourceAssetRetriever(TemplateFileEntity templateFileEntity);
}
