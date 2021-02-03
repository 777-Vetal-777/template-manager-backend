package com.itextpdf.dito.manager.util;

import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;

public final class TemplateDeploymentUtils {

    public static String getTemplateAliasForDefaultInstance(final TemplateFileEntity templateFileEntity){
        final String versionAliasSuffix = "_version-";
        final TemplateEntity templateEntity = templateFileEntity.getTemplate();
        return new StringBuilder().append(templateEntity.getName())
                .append(versionAliasSuffix)
                .append(templateFileEntity.getVersion())
                .toString();
    }

    private TemplateDeploymentUtils() {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }
}
