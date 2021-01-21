package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.template.TemplateDependencyService;
import com.itextpdf.dito.manager.service.template.TemplatePermissionService;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateVersionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.List;
import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;

@RestController
public class TemplateControllerImpl extends AbstractController implements TemplateController {
    private final TemplateService templateService;
    private final TemplatePreviewGenerator templatePreviewGenerator;
    private final DataCollectionFileService dataCollectionFileService;
    private final TemplateMapper templateMapper;
    private final DependencyMapper dependencyMapper;
    private final TemplateVersionsService templateVersionsService;
    private final TemplatePermissionService templatePermissionService;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final TemplateDependencyService templateDependencyService;
    private final FileVersionMapper fileVersionMapper;

    public TemplateControllerImpl(final TemplateService templateService,
                                  final TemplatePreviewGenerator templatePreviewGenerator, final DataCollectionFileService dataCollectionFileService,
                                  final TemplateMapper templateMapper,
                                  final DependencyMapper dependencyMapper,
                                  final TemplateVersionsService templateVersionsService,
                                  final TemplatePermissionService templatePermissionService,
                                  final PermissionMapper permissionMapper,
                                  final TemplateDependencyService templateDependencyService,
                                  final FileVersionMapper fileVersionMapper,
                                  final RoleMapper roleMapper) {
        this.templateService = templateService;
        this.templatePreviewGenerator = templatePreviewGenerator;
        this.dataCollectionFileService = dataCollectionFileService;
        this.templateMapper = templateMapper;
        this.dependencyMapper = dependencyMapper;
        this.templateVersionsService = templateVersionsService;
        this.templatePermissionService = templatePermissionService;
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.fileVersionMapper = fileVersionMapper;
        this.templateDependencyService = templateDependencyService;
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
        return new ResponseEntity<>(dependencyMapper.map(templateDependencyService
                .list(name)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> listDependenciesPageable(final String name,
                                                                        final Pageable pageable,
                                                                        final DependencyFilter dependencyFilter,
                                                                        final String searchParam) {
        return new ResponseEntity<>(dependencyMapper.map(templateDependencyService
                .list(pageable, name, dependencyFilter, searchParam)), HttpStatus.OK);
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
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Pageable pageable, final String name,
                                                            final VersionFilter versionFilter, final String searchParam) {

        return new ResponseEntity<>(fileVersionMapper
                .map(templateVersionsService.list(pageable, decodeBase64(name), versionFilter, searchParam)),
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
    public ResponseEntity<Page<TemplatePermissionDTO>> getRoles(final Pageable pageable, final String name, final TemplatePermissionFilter filter, final String search) {
        final Page<TemplatePermissionsModel> entities = templatePermissionService.getRoles(pageable, decodeBase64(name), filter, search);
        return new ResponseEntity<>(permissionMapper.mapTemplatePermissions(entities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> applyRole(final Principal principal, final String name,
                                                         @Valid final ApplyRoleRequestDTO applyRoleRequestDTO) {
        final TemplateEntity templateEntity = templateService
                .applyRole(decodeBase64(name), applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions(), principal.getName());
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> deleteRole(final Principal principal, final String name, final String roleName) {
        final TemplateEntity templateEntity = templateService
                .detachRole(decodeBase64(name), decodeBase64(roleName), principal.getName());
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity), HttpStatus.OK);
    }
}
