package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplateVersionDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.exception.resource.UnreadableResourceException;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateVersionsService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TemplateControllerImpl extends AbstractController implements TemplateController {
    private final TemplateService templateService;
    private final TemplatePreviewGenerator templatePreviewGenerator;
    private final DataCollectionService dataCollectionService;
    private final TemplateMapper templateMapper;
    private final DependencyMapper dependencyMapper;
    private final TemplateVersionsService templateVersionsService;

    public TemplateControllerImpl(final TemplateService templateService,
                                  final TemplatePreviewGenerator templatePreviewGenerator, final DataCollectionService dataCollectionService,
                                  final TemplateMapper templateMapper,
                                  final DependencyMapper dependencyMapper,
                                  final TemplateVersionsService templateVersionsService) {
        this.templateService = templateService;
        this.templatePreviewGenerator = templatePreviewGenerator;
        this.dataCollectionService = dataCollectionService;
        this.templateMapper = templateMapper;
        this.dependencyMapper = dependencyMapper;
        this.templateVersionsService = templateVersionsService;
    }

    @Override
    public ResponseEntity<TemplateDTO> create(@Valid final TemplateCreateRequestDTO templateCreateRequestDTO,
                                              final Principal principal) {
        final TemplateEntity templateEntity = templateService
                .create(templateCreateRequestDTO.getName(), templateCreateRequestDTO.getType(),
                        templateCreateRequestDTO.getDataCollectionName(), principal.getName());
        return new ResponseEntity<>(templateMapper.map(templateEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<TemplateDTO>> listTemplateTypes(final Pageable pageable,
                                                               final TemplateFilter templateFilter,
                                                               final String searchParam) {
        return new ResponseEntity<>(templateMapper.map(templateService.getAll(pageable, templateFilter, searchParam)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateTypeEnum[]> listTemplateTypes() {
        return new ResponseEntity<>(TemplateTypeEnum.values(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> listDependencies(final String name) {
        final List<DependencyDTO> dependencies = new ArrayList<>();

        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .getByTemplateName(decodeBase64(name));
        if (dataCollectionEntity != null) {
            dependencies.add(dependencyMapper.map(dataCollectionEntity));
        }

        return new ResponseEntity<>(dependencies, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> listDependenciesPageable(final String name,
                                                                final Pageable pageable,
                                                                final DependencyFilter dependencyFilter,
                                                                final String searchParam) {
        final List<DependencyDTO> dependencies = new ArrayList<>();

        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .getByTemplateName(decodeBase64(name));
        if (dataCollectionEntity != null) {
            dependencies.add(dependencyMapper.map(dataCollectionEntity));
        }

        return new ResponseEntity<>(new PageImpl<>(dependencies), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> get(final String name) {
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateService.get(decodeBase64(name))),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> update(final String name,
                                                      @Valid final TemplateUpdateRequestDTO templateUpdateRequestDTO, final Principal principal) {
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateService
                .update(decodeBase64(name), templateMapper.map(templateUpdateRequestDTO), principal.getName())),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<TemplateVersionDTO>> getVersions(final Pageable pageable, final String name,
                                                                final VersionFilter versionFilter, final String searchParam) {

        return new ResponseEntity<>(templateMapper
                .mapVersions(templateVersionsService.list(pageable, decodeBase64(name), versionFilter, searchParam)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> preview(final String templateName) {
        final String decodedTemplateName = decodeBase64(templateName);
        final ByteArrayOutputStream pdfStream = (ByteArrayOutputStream) templatePreviewGenerator
                .generate(decodedTemplateName);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        final String filename = new StringBuilder().append(decodedTemplateName).append(".pdf").toString();
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateDTO> create(final Principal principal,
                                              final String name,
                                              final String comment,
                                              final MultipartFile templateFile) {
        final byte[] data = templateFile != null ? getFileBytes(templateFile) : null;
        final TemplateEntity templateEntity = templateService
                .createNewVersion(name, data, principal.getName(),
                        comment);
        return new ResponseEntity<>(templateMapper.map(templateEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<RoleDTO>> getRoles(Pageable pageable, String name, RoleFilter filter) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private byte[] getFileBytes(final MultipartFile file) {
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            throw new UnreadableResourceException(file.getOriginalFilename());
        }
        return data;
    }
}
