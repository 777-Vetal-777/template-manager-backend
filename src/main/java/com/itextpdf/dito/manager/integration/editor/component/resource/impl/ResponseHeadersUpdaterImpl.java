package com.itextpdf.dito.manager.integration.editor.component.resource.impl;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;
import com.itextpdf.dito.manager.entity.resource.ResourceFileEntity;
import com.itextpdf.dito.manager.integration.editor.component.resource.ContentTypeHeaderMapper;
import com.itextpdf.dito.manager.integration.editor.component.resource.ResponseHeadersUpdater;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ResponseHeadersUpdaterImpl implements ResponseHeadersUpdater {

    private final Map<ResourceTypeEnum, ContentTypeHeaderMapper> contentTypeHeaderMappers;

    public ResponseHeadersUpdaterImpl(final List<ContentTypeHeaderMapper> contentTypeHeaderMappers) {
        this.contentTypeHeaderMappers = contentTypeHeaderMappers.stream().collect(Collectors.toMap(ContentTypeHeaderMapper::getType, Function.identity()));
    }

    @Override
    public HttpHeaders updateHeaders(final ResourceFileEntity entity,
                                     final ResourceTypeEnum type,
                                     final HttpHeaders headers) {
        final String contentType = Optional.ofNullable(contentTypeHeaderMappers.get(type)).map(mapper -> getContentType(entity, mapper)).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return headers;
    }

    private String getContentType(final ResourceFileEntity entity,
                                  final ContentTypeHeaderMapper mapper) {
        return mapper.map(entity.getFileName(), entity.getFile());
    }
}
