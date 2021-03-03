package com.itextpdf.dito.manager.service.resource;

import com.itextpdf.dito.editor.server.common.core.stream.Streamable;
import com.itextpdf.dito.editor.server.common.elements.urigenerator.ResourceUriGenerator;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;

import java.io.IOException;

public interface ResourceImportService {

    ResourceEntity importResource(Streamable stream, String name, String uri, String email) throws IOException;

    ResourceUriGenerator getResourceUriGenerator(String fileName);
}
