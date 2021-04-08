package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.create.DataSampleCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilter;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.dto.permission.DataCollectionPermissionDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionFileSizeExceedLimitException;
import com.itextpdf.dito.manager.exception.datacollection.NoSuchDataCollectionTypeException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionPermissionFilter;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionModelWithRoles;
import com.itextpdf.dito.manager.model.datacollection.DataCollectionPermissionsModel;
import com.itextpdf.dito.manager.model.dependency.DependencyModel;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionDependencyService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionPermissionService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import com.itextpdf.dito.manager.service.datasample.DataSampleFileService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class DataCollectionControllerImpl extends AbstractController implements DataCollectionController {
    private static final Logger log = LogManager.getLogger(DataCollectionControllerImpl.class);
    private final DataCollectionService dataCollectionService;
    private final DataCollectionMapper dataCollectionMapper;
    private final DataCollectionFileService dataCollectionFileService;
    private final DataCollectionDependencyService dataCollectionDependencyService;
    private final DependencyMapper dependencyMapper;
    private final PermissionMapper permissionMapper;
    private final DataSampleMapper dataSampleMapper;
    private final DataCollectionPermissionService dataCollectionPermissionService;
    private final DataSampleService dataSampleService;
    private final DataSampleFileService dataSampleFileService;
    private final FileVersionMapper fileVersionMapper;
    private final Encoder encoder;
    private final Long sizeJsonLimit;

    public DataCollectionControllerImpl(final DataCollectionService dataCollectionService,
                                        final DataCollectionMapper dataCollectionMapper,
                                        final DataCollectionFileService dataCollectionFileService,
                                        final DataCollectionDependencyService dataCollectionDependencyService,
                                        final DependencyMapper dependencyMapper,
                                        final PermissionMapper permissionMapper,
                                        final DataSampleMapper dataSampleMapper,
                                        final DataCollectionPermissionService dataCollectionPermissionService,
                                        final DataSampleService dataSampleService,
                                        final DataSampleFileService dataSampleFileService,
                                        final FileVersionMapper fileVersionMapper,
                                        final Encoder encoder,
                                        @Value("${data-collection.json.size-limit}") final Long sizeJsonLimit) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionMapper = dataCollectionMapper;
        this.dependencyMapper = dependencyMapper;
        this.dataCollectionFileService = dataCollectionFileService;
        this.dataCollectionDependencyService = dataCollectionDependencyService;
        this.permissionMapper = permissionMapper;
        this.dataSampleMapper = dataSampleMapper;
        this.dataCollectionPermissionService = dataCollectionPermissionService;
        this.dataSampleService = dataSampleService;
        this.fileVersionMapper = fileVersionMapper;
        this.sizeJsonLimit = sizeJsonLimit;
        this.dataSampleFileService = dataSampleFileService;
        this.encoder = encoder;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final String name, final String dataCollectionType, final MultipartFile multipartFile, final Principal principal) {
        log.info("Started creating new dataCollection by name: {} and type: {}", name, dataCollectionType);
        final DataCollectionType collectionType = getDataCollectionTypeFromPath(dataCollectionType);
        final byte[] data = getBytesFromMultipart(multipartFile);
        checkFileSizeIsNotExceededLimit(multipartFile.getSize());
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.create(name, collectionType, data, multipartFile.getOriginalFilename(), principal.getName());
        log.info("Creating new dataCollection by name: {} and type: {} is finished successfully", name, dataCollectionType);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(dataCollectionEntity, principal.getName()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> listDependencies(final String name) {
        log.info("Started getting list of Dependencies by name: {}", name);
        final String decodedName = encoder.decode(name);
        final List<DependencyDTO> dependencyDTOs = dependencyMapper.map(dataCollectionDependencyService.list(decodedName));
        log.info("get listOfDependencies by name: {} is finished successfully", name);
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final Principal principal, final String name, final String dataCollectionType, final MultipartFile multipartFile, final String comment) {
        log.info("Started creating new version of dataCollection with name: {}, and type: {}", name, dataCollectionType);
        final DataCollectionType collectionType = getDataCollectionTypeFromPath(dataCollectionType);
        final byte[] data = getBytesFromMultipart(multipartFile);
        checkFileSizeIsNotExceededLimit(multipartFile.getSize());
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.createNewVersion(name, collectionType, data, multipartFile.getOriginalFilename(), principal.getName(), comment);
        log.info("Create new version of dataCollection with name: {} is finished successfully", name);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(dataCollectionEntity, principal.getName()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final DataCollectionFilter filter,
                                                        final String searchParam, final Principal principal) {
        log.info("Get list of dataCollections with params: {} and searchParam: {} was started", filter, searchParam);
        final Page<DataCollectionModelWithRoles> dataCollectionModelWithRoles = dataCollectionService.listDataCollectionModel(pageable, filter, searchParam);
        log.info("Get list of dataCollections with params: {} and searchParam: {} was finished successfully", filter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.mapModels(dataCollectionModelWithRoles, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DataCollectionDTO>> list(final DataCollectionFilter filter, final String searchParam, final Principal principal) {
        log.info("Started getting list of dataCollections with params: {} and searchParam: {}", filter, searchParam);
        final List<DataCollectionEntity> dataCollectionEntities = dataCollectionService.list(filter, searchParam);
        log.info("Getting list of dataCollections with params: {} and searchParam: {} is finished successfully", filter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntities, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> get(final String name, final Principal principal) {
        log.info("Get dataCollection by name: {} was started", name);
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.get(encoder.decode(name));
        log.info("Get dataCollection by name: {} was finished successfully", name);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(dataCollectionEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> update(final String name,
                                                    final @Valid DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
                                                    final Principal principal) {
        log.info("Update dataCollection by name: {} and params: {} was started", name, dataCollectionUpdateRequestDTO);
        final DataCollectionEntity entity = dataCollectionService.update(encoder.decode(name), dataCollectionMapper.map(dataCollectionUpdateRequestDTO),
                principal.getName());
        log.info("Update dataCollection by name: {} and params: {} was finished successfully", name, dataCollectionUpdateRequestDTO);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(entity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name, final Principal principal) {
        log.info("Delete by name: {} was started", name);
        dataCollectionService.delete(encoder.decode(name), principal.getName());
        log.info("Delete by name: {} was finished successfully", name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Pageable pageable,
                                                            final String name,
                                                            final VersionFilter versionFilter,
                                                            final String searchParam) {
        log.info("Get dataCollection versions by name: {} and params: {} was started", name, versionFilter);
        final String dataCollectionName = encoder.decode(name);
        final Page<FileVersionModel> dataCollectionVersionEntities = dataCollectionFileService.list(pageable, dataCollectionName, versionFilter, searchParam);
        log.info("Get dataCollection versions by name: {} and params: {} was finished successfully", name, versionFilter);
        return new ResponseEntity<>(fileVersionMapper.map(dataCollectionVersionEntities), HttpStatus.OK);
    }

    private DataCollectionType getDataCollectionTypeFromPath(final String dataCollectionType) {
        if (!EnumUtils.isValidEnum(DataCollectionType.class, dataCollectionType)) {
            throw new NoSuchDataCollectionTypeException(dataCollectionType);
        }
        return DataCollectionType.valueOf(dataCollectionType);
    }

    @Override
    public ResponseEntity<Page<DependencyDTO>> listDependencies(final Pageable pageable,
                                                                final String name,
                                                                final DependencyFilter filter,
                                                                final String searchParam) {
        log.info("Get list dependencies by name: {} and params: {} was started", name, filter);
        final String dataCollectionName = encoder.decode(name);
        final Page<DependencyModel> result = dataCollectionDependencyService.list(pageable, dataCollectionName, filter, searchParam);
        log.info("Get list dependencies by name: {} and params: {} was finished successfully", name, filter);
        return new ResponseEntity<>(dependencyMapper.map(result), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DataCollectionPermissionDTO>> getRoles(final Pageable pageable, final String name, final DataCollectionPermissionFilter filter, final String searchParam) {
        log.info("Get list dataCollection's roles by dataCollectionName: {} and params: {} was started", name, filter);
        final Page<DataCollectionPermissionsModel> roleEntities = dataCollectionPermissionService
                .getRoles(pageable, encoder.decode(name), filter, searchParam);
        log.info("Get list dataCollection's roles by dataCollectionName: {} and params: {} was finished successfully", name, filter);
        return new ResponseEntity<>(permissionMapper.mapDataCollectionPermissions(roleEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> applyRole(final String name,
                                                       @Valid final ApplyRoleRequestDTO applyRoleRequestDTO, final Principal principal) {
        log.info("ApplyRole by dataCollectionName: {} and newRoleParams: {} was started", name, applyRoleRequestDTO);
        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .applyRole(encoder.decode(name), applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions());
        log.info("ApplyRole by dataCollectionName: {} and newRoleParams: {} was finished successfully", name, applyRoleRequestDTO);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity, principal.getName()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> deleteRole(final String name, final String roleName, final  Principal principal) {
        log.info("Delete role by dataCollectionName: {} and roleName: {} was started", name, roleName);
        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .detachRole(encoder.decode(name), encoder.decode(roleName));
        log.info("Delete role by dataCollectionName: {} and roleName: {} was finished successfully", name, roleName);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity, principal.getName()), HttpStatus.OK);

    }

    private void checkFileSizeIsNotExceededLimit(final Long fileSize) {
        if (fileSize > sizeJsonLimit) {
            throw new DataCollectionFileSizeExceedLimitException(fileSize);
        }
    }

    @Override
    public ResponseEntity<DataSampleDTO> create(final String dataCollectionName, final @Valid DataSampleCreateRequestDTO dataSampleCreateRequestDTO, final Principal principal) {
        log.info("Create dataSample by dataCollectionName {} and dataSampleParams: {} was started", dataCollectionName, dataSampleCreateRequestDTO);
        final String dataSampleName = dataSampleCreateRequestDTO.getName();
        final String fileName = dataSampleCreateRequestDTO.getFileName();
        final String data = dataSampleCreateRequestDTO.getSample();
        final String comment = dataSampleCreateRequestDTO.getComment();
        final DataSampleEntity dataSampleEntity = dataCollectionService.create(encoder.decode(dataCollectionName), dataSampleName, fileName, data, comment, principal.getName());
        log.info("Create dataSample by dataCollectionName {} and dataSampleParams: {} was finished successfully", dataCollectionName, dataSampleCreateRequestDTO);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataSampleDTO>> listDataSamples(final String dataCollectionName, final Pageable pageable,
                                                               final DataSampleFilter filter, final String searchParam) {
        log.info("Get list dataSamples by dataCollectionName: {} and filter: {} and searchParam: {} was started", dataCollectionName, filter, searchParam);
        final DataCollectionEntity dataCollection = dataCollectionService.get(encoder.decode(dataCollectionName));
        log.info("Get list dataSamples by dataCollectionName: {} and filter: {} and searchParam: {} was finished successfully", dataCollectionName, filter, searchParam);
        return new ResponseEntity<>(dataSampleMapper.map(dataSampleService.list(pageable, dataCollection.getId(), filter, searchParam)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DataSampleDTO>> listDataSamples(final String dataCollectionName) {
        log.info("Get list dataSamples by dataCollectionName: {} was started", dataCollectionName);
        final DataCollectionEntity dataCollection = dataCollectionService.get(encoder.decode(dataCollectionName));
        log.info("Get list dataSamples by dataCollectionName: {} was finished successfully", dataCollectionName);
        return new ResponseEntity<>(dataSampleMapper.map(dataSampleService.list(dataCollection.getId())),
                HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<DataSampleDTO>> listDataSamplesByTemplateName(final String templateName) {
        log.info("Get list dataSamples by templateName: {} was started", templateName);
        final List<DataSampleEntity> listByTemplateName = dataSampleService.getListByTemplateName(encoder.decode(templateName));
        log.info("Get list dataSamples by templateName: {} was finished successfully", templateName);
        return new ResponseEntity<>(dataSampleMapper.map(listByTemplateName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> getDataSample(final String dataCollectionName, final String dataSampleName) {
        log.info("Get dataSample by dataCollectionName: {} and dataSampleName: {} was started", dataCollectionName, dataSampleName);
        final DataSampleEntity dataSampleEntity = dataSampleService.get(encoder.decode(dataCollectionName), encoder.decode(dataSampleName));
        log.info("Get dataSample by dataCollectionName: {} and dataSampleName: {} was finished successfully", dataCollectionName, dataSampleName);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteDataSampleList(final String dataCollectionName,
                                                     final List<String> dataSampleNames, final Principal principal) {
        log.info("Delete list dataSamples by dataSamplesNames: {} was started", dataSampleNames);
        dataSampleService.delete(encoder.decode(dataCollectionName), dataSampleNames);
        log.info("Delete list dataSamples by dataSamplesNames: {} was finished successfully", dataSampleNames);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteAllDataSamples(final String dataCollectionName, final Principal principal) {
        log.info("Delete all dataSamples by dataCollectionName: {} was started", dataCollectionName);
        dataSampleService.delete(dataCollectionService.get(encoder.decode(dataCollectionName)));
        log.info("Delete all dataSamples by dataCollectionName: {} was finished successfully", dataCollectionName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> setDataSampleAsDefault(final String dataCollectionName, final String dataSampleName,
                                                                final Principal principal) {
        log.info("Set dataSample as default by dataCollectionName: {} and dataSampleName: {} was started", dataCollectionName, dataSampleName);
        final DataSampleEntity dataSampleEntity = dataSampleService.setAsDefault(encoder.decode(dataCollectionName), encoder.decode(dataSampleName));
        log.info("Set dataSample as default by dataCollectionName: {} and dataSampleName: {} was finished successfully", dataCollectionName, dataSampleName);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> updateDataSample(final String dataCollectionName, final String dataSampleName,
                                                          @Valid final DataSampleUpdateRequestDTO dataSampleUpdateRequestDTO, final Principal principal) {
        log.info("Update dataSample by dataSampleName: {} and dataSampleUpdateRequestDTO: {} was started", dataSampleName, dataSampleUpdateRequestDTO);
        final DataSampleEntity entity = dataSampleService.update(encoder.decode(dataCollectionName), encoder.decode(dataSampleName), dataSampleMapper.map(dataSampleUpdateRequestDTO), principal.getName());
        log.info("Update dataSample by dataSampleName: {} and dataSampleUpdateRequestDTO: {} was finished successfully", dataSampleName, dataSampleUpdateRequestDTO);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> createDataSampleNewVersion(final String dataCollectionName,
                                                                    final @Valid DataSampleCreateRequestDTO dataSampleCreateRequestDTO, final Principal principal) {
        final String decodedDataCollectionName = encoder.decode(dataCollectionName);
        log.info("Create new version of dataSample: {} was started", dataSampleCreateRequestDTO);
        final String dataSampleName = dataSampleCreateRequestDTO.getName();
        final String fileName = dataSampleCreateRequestDTO.getFileName();
        final String data = dataSampleCreateRequestDTO.getSample();
        final String comment = dataSampleCreateRequestDTO.getComment();
        final DataSampleEntity dataSampleEntity = dataSampleService.createNewVersion(decodedDataCollectionName, dataSampleName, data,
                fileName, principal.getName(), comment);
        log.info("Create new version of dataSample: {} was finished successfully", dataSampleCreateRequestDTO);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getDataSampleVersions(final Pageable pageable,
                                                                      final String dataCollectionName, final String name, final VersionFilter versionFilter,
                                                                      final String searchParam) {
        log.info("Get dataSampleVersions by dataSampleName: {} and filter: {}", name, versionFilter);
        final String dataSampleName = encoder.decode(name);
        final Page<FileVersionModel> dataSampleVersionEntities = dataSampleFileService.list(pageable, dataSampleName,
                versionFilter, searchParam);
        log.info("Get dataSampleVersions by dataSampleName: {} and filter: {}", name, versionFilter);
        return new ResponseEntity<>(fileVersionMapper.map(dataSampleVersionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> rollback(final Principal principal, final String name, final Long version) {
        log.info("Rollback by dataCollectionName: {} and version: {} was started ", name, version);
        final DataCollectionEntity result = dataCollectionService.rollbackVersion(encoder.decode(name), version, principal.getName());
        log.info("Rollback by dataCollectionName: {} and version: {} was finished successfully", name, version);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(result, principal.getName()), HttpStatus.OK);
    }
}
