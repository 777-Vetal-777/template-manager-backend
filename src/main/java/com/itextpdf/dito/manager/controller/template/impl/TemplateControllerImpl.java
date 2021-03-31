package com.itextpdf.dito.manager.controller.template.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.mapper.template.TemplateMapper;
import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.template.TemplateController;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.stage.StageDTO;
import com.itextpdf.dito.manager.dto.template.TemplateDTO;
import com.itextpdf.dito.manager.dto.template.TemplateMetadataDTO;
import com.itextpdf.dito.manager.dto.template.TemplatePermissionDTO;
import com.itextpdf.dito.manager.dto.template.TemplateWithSettingsDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplateCreateRequestDTO;
import com.itextpdf.dito.manager.dto.template.create.TemplatePartDTO;
import com.itextpdf.dito.manager.dto.template.setting.SettingType;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportNameModel;
import com.itextpdf.dito.manager.dto.template.setting.TemplateImportSettingDTO;
import com.itextpdf.dito.manager.dto.template.update.TemplateUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.template.version.TemplateDeployedVersionDTO;
import com.itextpdf.dito.manager.entity.StageEntity;
import com.itextpdf.dito.manager.entity.TemplateTypeEnum;
import com.itextpdf.dito.manager.entity.template.TemplateEntity;
import com.itextpdf.dito.manager.entity.template.TemplateFileEntity;
import com.itextpdf.dito.manager.exception.template.TemplateExtensionNotSupportedException;
import com.itextpdf.dito.manager.filter.template.TemplateFilter;
import com.itextpdf.dito.manager.filter.template.TemplateListFilter;
import com.itextpdf.dito.manager.filter.template.TemplatePermissionFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.model.template.TemplateModelWithRoles;
import com.itextpdf.dito.manager.model.template.TemplatePermissionsModel;
import com.itextpdf.dito.manager.service.template.TemplateDependencyService;
import com.itextpdf.dito.manager.service.template.TemplateDeploymentService;
import com.itextpdf.dito.manager.service.template.TemplateExportService;
import com.itextpdf.dito.manager.service.template.TemplateImportService;
import com.itextpdf.dito.manager.service.template.TemplatePermissionService;
import com.itextpdf.dito.manager.service.template.TemplatePreviewGenerator;
import com.itextpdf.dito.manager.service.template.TemplateService;
import com.itextpdf.dito.manager.service.template.TemplateVersionsService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;
import static com.itextpdf.dito.sdk.core.pkg.PackageConstant.TEMPLATE_PACKAGE_EXTENSION;

@RestController
public class TemplateControllerImpl extends AbstractController implements TemplateController {
    private static final Logger log = LogManager.getLogger(TemplateControllerImpl.class);
    private final List<String> supportedExtensions;
    private final TemplateService templateService;
    private final TemplateMapper templateMapper;
    private final DependencyMapper dependencyMapper;
    private final TemplateVersionsService templateVersionsService;
    private final TemplatePermissionService templatePermissionService;
    private final PermissionMapper permissionMapper;
    private final TemplateDependencyService templateDependencyService;
    private final FileVersionMapper fileVersionMapper;
    private final TemplateDeploymentService templateDeploymentService;
    private final TemplatePreviewGenerator templatePreviewGenerator;
    private final TemplateExportService templateExportService;
    private final WorkspaceMapper workspaceMapper;
    private final TemplateImportService templateImportService;
    private final Encoder encoder;

    public TemplateControllerImpl(@Value("${template.extensions.supported}") final List<String> supportedExtensions,
                                  final TemplateService templateService,
                                  final TemplateMapper templateMapper,
                                  final DependencyMapper dependencyMapper,
                                  final TemplateVersionsService templateVersionsService,
                                  final TemplatePermissionService templatePermissionService,
                                  final PermissionMapper permissionMapper,
                                  final TemplateDependencyService templateDependencyService,
                                  final FileVersionMapper fileVersionMapper,
                                  final TemplateDeploymentService templateDeploymentService,
                                  final TemplatePreviewGenerator templatePreviewGenerator,
                                  final TemplateExportService templateExportService,
                                  final WorkspaceMapper workspaceMapper,
                                  final TemplateImportService templateImportService,
                                  final Encoder encoder) {
        this.supportedExtensions = supportedExtensions;
        this.templateService = templateService;
        this.templateMapper = templateMapper;
        this.dependencyMapper = dependencyMapper;
        this.templateVersionsService = templateVersionsService;
        this.templatePermissionService = templatePermissionService;
        this.permissionMapper = permissionMapper;
        this.fileVersionMapper = fileVersionMapper;
        this.templateDependencyService = templateDependencyService;
        this.templateDeploymentService = templateDeploymentService;
        this.templatePreviewGenerator = templatePreviewGenerator;
        this.templateExportService = templateExportService;
        this.workspaceMapper = workspaceMapper;
        this.templateImportService = templateImportService;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<TemplateDTO> create(@Valid final TemplateCreateRequestDTO templateCreateRequestDTO,
                                              final Principal principal) {
        log.info("Create template with params: {} was started", templateCreateRequestDTO);
        final TemplateEntity templateEntity = templateService
                .create(templateCreateRequestDTO.getName(), templateCreateRequestDTO.getType(),
                        templateCreateRequestDTO.getDataCollectionName(), principal.getName(), templateMapper.mapPartDto(templateCreateRequestDTO.getTemplateParts()));
        log.info("Create template with params: {} was finished successfully", templateCreateRequestDTO);
        return new ResponseEntity<>(templateMapper.map(templateEntity, principal.getName()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<TemplateWithSettingsDTO>> listCompositionTemplates(final String name, final Principal principal) {
        log.info("Get list composition templates by template name: {} was started", name);
        final List<TemplateWithSettingsDTO> templateDTOS = templateMapper.mapTemplatesWithPart(templateService.getAllParts(encoder.decode(name)), principal.getName());
        log.info("Get list composition templates by template name: {} was finished successfully", name);
        return new ResponseEntity<>(templateDTOS, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<TemplateDTO>> listTemplates(final Pageable pageable,
                                                           final TemplateFilter templateFilter,
                                                           final String searchParam, final Principal principal) {
        log.info("Get list templates with pageable by filter: {} and searchParam: {} was started", templateFilter, searchParam);
        final Page<TemplateModelWithRoles> templateModelWithRoles = templateService.getAll(pageable, templateFilter, searchParam);
        log.info("Get list templates with pageable by filter: {} and searchParam: {} was finished successfully", templateFilter, searchParam);
        return new ResponseEntity<>(templateMapper.mapModels(templateModelWithRoles, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TemplateDTO>> listTemplates(final TemplateListFilter templateListFilter, final Principal principal) {
        log.info("Get list templates by filter: {} was started", templateListFilter);
        final List<TemplateEntity> templates = templateService.getAll(templateListFilter);
        log.info("Get list templates by filter: {} was finished successfully", templateListFilter);
        return new ResponseEntity<>(templateMapper.map(templates, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateTypeEnum[]> listTemplateTypes() {
        return new ResponseEntity<>(TemplateTypeEnum.values(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> listDependencies(final String name) {
        log.info("Get list template dependencies by name: {} was started", name);
        final List<DependencyModel> dependencyModels = templateDependencyService.list(name);
        log.info("Get list template dependencies by name: {} was finished successfully", name);
        return new ResponseEntity<>(dependencyMapper.map(dependencyModels), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> listDependenciesPageable(final String name,
                                                                        final Pageable pageable,
                                                                        final DependencyFilter dependencyFilter,
                                                                        final String searchParam) {
        log.info("Get list template dependencies by name: {} with pageable was started", name);
        final Page<DependencyModel> dependencyModels = templateDependencyService.list(pageable, name, dependencyFilter, searchParam);
        log.info("Get list template dependencies by name: {} with pageable was finished successfully", name);
        return new ResponseEntity<>(dependencyMapper.map(dependencyModels), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> get(final String name, final Principal principal) {
        log.info("Get template by name: {} was started", name);
        final TemplateEntity templateEntity = templateService.get(encoder.decode(name));
        log.info("Get template by name: {} was finished successfully", name);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> update(final String name,
                                                      @Valid final TemplateUpdateRequestDTO templateUpdateRequestDTO, final Principal principal) {
        log.info("Update template by name: {} and params: {} was started", name, templateUpdateRequestDTO);
        final TemplateEntity templateEntity = templateService
                .update(encoder.decode(name), templateMapper.map(templateUpdateRequestDTO), principal.getName());
        log.info("Update template by name: {} and params: {} was finished successfully", name, templateUpdateRequestDTO);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Pageable pageable, final String name,
                                                            final VersionFilter versionFilter, final String searchParam) {
        log.info("Get template versions by name: {} and filter: {} was started", name, versionFilter);
        final Page<FileVersionModel> versions = templateVersionsService.list(pageable, encoder.decode(name), versionFilter, searchParam);
        log.info("Get template versions by name: {} and filter: {} was finished successfully", name, versionFilter);
        return new ResponseEntity<>(fileVersionMapper.map(versions),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> preview(final String templateName, final String dataSampleName) {
        log.info("Get template preview by templateName: {} and dataSampleName: {} was started", templateName, dataSampleName);
        final String decodedTemplateName = encoder.decode(templateName);
        final ByteArrayOutputStream pdfStream = templatePreviewGenerator.generatePreview(decodedTemplateName, dataSampleName);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        final String filename = new StringBuilder().append(decodedTemplateName).append(".pdf").toString();
        headers.setContentDispositionFormData("attachment", filename);
        log.info("Get template preview by templateName: {} and dataSampleName: {} was finished successfully", templateName, dataSampleName);
        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateDTO> createVersion(final Principal principal,
                                                     final String name,
                                                     final String comment,
                                                     final List<TemplatePartDTO> templateParts,
                                                     final MultipartFile templateFile) {
        log.info("Create new version of template with name: {} and comment: {} and templateParts: {} was started", name, comment, templateParts);
        final byte[] data = templateFile != null ? getFileBytes(templateFile) : null;
        final TemplateEntity templateEntity = templateService
                .createNewVersion(name, data, principal.getName(),
                        comment, null, templateMapper.mapPartDto(templateParts));
        log.info("Create new version of template with name: {} and comment: {} and templateParts: {} was finished successfully", name, comment, templateParts);
        return new ResponseEntity<>(templateMapper.map(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<TemplatePermissionDTO>> getRoles(final Pageable pageable, final String name, final TemplatePermissionFilter filter, final String search) {
        log.info("Get template's roles by name: {} and filter: {} and searchParam: {} was started", name, filter, search);
        final Page<TemplatePermissionsModel> entities = templatePermissionService.getRoles(pageable, encoder.decode(name), filter, search);
        log.info("Get template's roles by name: {} and filter: {} and searchParam: {} was finished successfully", name, filter, search);
        return new ResponseEntity<>(permissionMapper.mapTemplatePermissions(entities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> applyRole(final Principal principal, final String name,
                                                         @Valid final ApplyRoleRequestDTO applyRoleRequestDTO) {
        log.info("Apply custom role to a template by templateName: {} and params: {} was started", name, applyRoleRequestDTO);
        final TemplateEntity templateEntity = templateService
                .applyRole(encoder.decode(name), applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions(), principal.getName());
        log.info("Apply custom role to a template by templateName: {} and params: {} was finished successfully", name, applyRoleRequestDTO);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> deleteRole(final Principal principal, final String name, final String roleName) {
        log.info("Delete role from a template by templateName {} and roleName: {} was started", name, roleName);
        final TemplateEntity templateEntity = templateService
                .detachRole(encoder.decode(name), encoder.decode(roleName), principal.getName());
        log.info("Delete role from a template by templateName {} and roleName: {} was finished successfully", name, roleName);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> block(final Principal principal, final String name) {
        log.info("Block template for editing by templateName: {} was started", name);
        final TemplateEntity templateEntity = templateService.block(principal.getName(), encoder.decode(name));
        log.info("Block template for editing by templateName: {} was finished successfully", name);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateMetadataDTO> unblock(final Principal principal, final String name) {
        log.info("Unblock template for editing by templateName: {} was started", name);
        final TemplateEntity templateEntity = templateService.unblock(principal.getName(), encoder.decode(name));
        log.info("Unblock template for editing by templateName: {} was finished successfully", name);
        return new ResponseEntity<>(templateMapper.mapToMetadata(templateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TemplateDeployedVersionDTO>> promote(final String name, final Long version) {
        log.info("Promote template version to next stage by name: {} was started", name);
        final TemplateFileEntity promotedVersion = templateDeploymentService.promote(encoder.decode(name), version);
        log.info("Promote template version to next stage by name: {} was finished successfully", name);
        return new ResponseEntity<>(templateMapper.mapToDeployedVersions(promotedVersion.getTemplate()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TemplateDeployedVersionDTO>> undeploy(final String name, final Long version) {
        log.info("Undeploy template version from promotion path by name: {} and version: {} was started", name, version);
        final TemplateFileEntity undeployedVersion = templateDeploymentService.undeploy(encoder.decode(name), version);
        log.info("Undeploy template version from promotion path by name: {} and version: {} was finished successfully", name, version);
        return new ResponseEntity<>(templateMapper.mapToDeployedVersions(undeployedVersion.getTemplate()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateDTO> rollbackVersion(final Principal principal, final String templateName, final Long templateVersion) {
        log.info("Rollback template version by templateName: {} and templateVersion: {} was started", templateName, templateVersion);
        final TemplateEntity updatedTemplateEntity = templateVersionsService.rollbackVersion(encoder.decode(templateName), templateVersion, principal.getName());
        log.info("Rollback template version by templateName: {} and templateVersion: {} was finished successfully", templateName, templateVersion);
        return new ResponseEntity<>(templateMapper.map(updatedTemplateEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String templateName) {
        log.info("Delete template by templateName: {} was started", templateName);
        templateService.delete(encoder.decode(templateName));
        log.info("Delete template by templateName: {} was finished successfully", templateName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StageDTO> getNextStage(final String templateName, final Long templateVersion) {
        log.info("Get next stage for template version by templateName: {} and templateVersion: {} was started", templateName, templateVersion);
        final StageEntity stageEntity = templateDeploymentService.getNextStage(encoder.decode(templateName), templateVersion);
        log.info("Get next stage for template version by templateName: {} and templateVersion: {} was finished successfully", templateName, templateVersion);
        return new ResponseEntity<>(workspaceMapper.map(stageEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> export(final String templateName, final Boolean dependenciesFlag) {
        log.info("Export template by templateName: {} and dependenciesFlag: {} was started", templateName, dependenciesFlag);
        final boolean exportDependencies = Optional.ofNullable(dependenciesFlag).orElse(true);
        final String decodedTemplateName = encoder.decode(templateName);
        final byte[] zippedProject = templateExportService.export(decodedTemplateName, exportDependencies);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final String filename = decodedTemplateName.concat(TEMPLATE_PACKAGE_EXTENSION);
        headers.setContentDispositionFormData("attachment", filename);
        log.info("Export template by templateName: {} and dependenciesFlag: {} was finished successfully", templateName, dependenciesFlag);
        return new ResponseEntity<>(zippedProject, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TemplateDTO> importData(final Principal principal, final MultipartFile templateFile, final List<TemplateImportSettingDTO> templateImportSettings) {
        log.info("Import template by fileName: {} was started", templateFile.getOriginalFilename());
        checkFileExtensionIsSupported(templateFile);
        final byte[] data = getFileBytes(templateFile);
        final Map<SettingType, Map<String, TemplateImportNameModel>> models = (templateImportSettings == null
                ? new EnumMap<>(SettingType.class)
                : templateImportSettings.stream().collect(Collectors.groupingBy(TemplateImportSettingDTO::getType, Collectors.mapping(a -> a, Collectors.toMap(TemplateImportNameModel::getName, a -> a)))));
        Arrays.stream(SettingType.values()).forEach(type -> models.putIfAbsent(type, Collections.emptyMap()));

        final String fileName = Optional.ofNullable(FilenameUtils.removeExtension(templateFile.getOriginalFilename())).map(file -> file.replace(' ', '_')).orElse("unknown").concat("-import");
        log.info("Import template by fileName: {} was finished successfully", templateFile.getOriginalFilename());
        return new ResponseEntity<>(templateMapper.map(templateImportService.importTemplate(fileName, data, principal.getName(), models), principal.getName()), HttpStatus.OK);
    }

    private void checkFileExtensionIsSupported(final MultipartFile resource) {
        final String templateExtension = Optional.ofNullable(FilenameUtils.getExtension(resource.getOriginalFilename())).map(String::toLowerCase).orElse("");
        if (!this.supportedExtensions.contains(templateExtension)) {
            throw new TemplateExtensionNotSupportedException(templateExtension);
        }
    }
}
