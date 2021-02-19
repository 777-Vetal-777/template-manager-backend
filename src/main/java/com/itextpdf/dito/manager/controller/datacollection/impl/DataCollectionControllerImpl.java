package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.component.mapper.dependency.DependencyMapper;
import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.component.mapper.permission.PermissionMapper;
import com.itextpdf.dito.manager.component.security.PermissionHandler;
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
    private final PermissionHandler permissionHandler;
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
                                        final PermissionHandler permissionHandler,
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
        this.permissionHandler = permissionHandler;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final String name, final String dataCollectionType, final MultipartFile multipartFile, final Principal principal) {
        final DataCollectionType collectionType = getDataCollectionTypeFromPath(dataCollectionType);
        final byte[] data = getBytesFromMultipart(multipartFile);
        checkFileSizeIsNotExceededLimit(multipartFile.getSize());
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.create(name, collectionType, data, multipartFile.getOriginalFilename(), principal.getName());
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(dataCollectionEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<DependencyDTO>> listDependencies(final String name) {
        final String decodedName = decodeBase64(name);
        final List<DependencyDTO> dependencyDTOs = dependencyMapper.map(dataCollectionDependencyService.list(decodedName));
        return new ResponseEntity<>(dependencyDTOs, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final Principal principal, final String name, final String dataCollectionType, final MultipartFile multipartFile, final String comment) {
        final DataCollectionType collectionType = getDataCollectionTypeFromPath(dataCollectionType);
        final byte[] data = getBytesFromMultipart(multipartFile);
        checkFileSizeIsNotExceededLimit(multipartFile.getSize());
        final DataCollectionEntity dataCollectionEntity = dataCollectionService.createNewVersion(name, collectionType, data, multipartFile.getOriginalFilename(), principal.getName(), comment);
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(dataCollectionEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final DataCollectionFilter filter,
                                                        final String searchParam) {

        final Page<DataCollectionEntity> dataCollectionEntities = dataCollectionService.list(pageable, filter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DataCollectionDTO>> list(DataCollectionFilter filter, String searchParam) {
        final List<DataCollectionEntity> dataCollectionEntities = dataCollectionService.list(filter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> get(final String name, final Principal principal) {
        DataCollectionDTO dataCollectionDTO = null;
        DataCollectionEntity dataCollectionEntity = dataCollectionService.get(decodeBase64(name));
        if(!permissionHandler.checkPermissionsByUser(principal.getName(), "E6_US39_TABLE_OF_DATA_COLLECTIONS_PERMISSIONS")){
            dataCollectionDTO = dataCollectionMapper.mapWithFileWithoutRoles(dataCollectionEntity);
        }else {
            dataCollectionDTO = dataCollectionMapper.mapWithFile(dataCollectionEntity);
        }
        return new ResponseEntity<>(dataCollectionDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> update(final String name,
                                                    final @Valid DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
                                                    final Principal principal) {
        final DataCollectionEntity entity = dataCollectionService.update(decodeBase64(name), dataCollectionMapper.map(dataCollectionUpdateRequestDTO),
                principal.getName());

        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name, final Principal principal) {
        dataCollectionService.delete(decodeBase64(name), principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getVersions(final Pageable pageable,
                                                            final String name,
                                                            final VersionFilter versionFilter,
                                                            final String searchParam) {
        final String dataCollectionName = decodeBase64(name);
        final Page<FileVersionModel> dataCollectionVersionEntities = dataCollectionFileService.list(pageable, dataCollectionName, versionFilter, searchParam);
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
        final String dataCollectionName = decodeBase64(name);
        final Page<DependencyModel> result = dataCollectionDependencyService.list(pageable, dataCollectionName, filter, searchParam);
        return new ResponseEntity<>(dependencyMapper.map(result), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DataCollectionPermissionDTO>> getRoles(final Pageable pageable, final String name, final DataCollectionPermissionFilter filter, final String searchParam) {
        final Page<DataCollectionPermissionsModel> roleEntities = dataCollectionPermissionService
                .getRoles(pageable, decodeBase64(name), filter, searchParam);
        return new ResponseEntity<>(permissionMapper.mapDataCollectionPermissions(roleEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> applyRole(final String name,
                                                       @Valid final ApplyRoleRequestDTO applyRoleRequestDTO) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .applyRole(decodeBase64(name), applyRoleRequestDTO.getRoleName(),
                        applyRoleRequestDTO.getPermissions());
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> deleteRole(final String name, final String roleName) {
        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .detachRole(decodeBase64(name), decodeBase64(roleName));
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity), HttpStatus.OK);

    }

    private void checkFileSizeIsNotExceededLimit(final Long fileSize) {
        if (fileSize > sizeJsonLimit) {
            throw new DataCollectionFileSizeExceedLimitException(fileSize);
        }
    }

    @Override
    public ResponseEntity<DataSampleDTO> create(final String dataCollectionName, final @Valid DataSampleCreateRequestDTO dataSampleCreateRequestDTO, final Principal principal) {
        final String dataSampleName = dataSampleCreateRequestDTO.getName();
        final String fileName = dataSampleCreateRequestDTO.getFileName();
        final String data = dataSampleCreateRequestDTO.getSample();
        final String comment = dataSampleCreateRequestDTO.getComment();
        final DataSampleEntity dataSampleEntity = dataCollectionService.create(decodeBase64(dataCollectionName), dataSampleName, fileName, data, comment, principal.getName());
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<DataSampleDTO>> listDataSamples(final String dataCollectionName, final Pageable pageable,
                                                               final DataSampleFilter filter, final String searchParam) {
        final DataCollectionEntity dataCollection = dataCollectionService.get(decodeBase64(dataCollectionName));
        return new ResponseEntity<>(dataSampleMapper.map(dataSampleService.list(pageable, dataCollection.getId(), filter, searchParam)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<DataSampleDTO>> listDataSamples(final String dataCollectionName) {
        final DataCollectionEntity dataCollection = dataCollectionService.get(decodeBase64(dataCollectionName));
        return new ResponseEntity<>(dataSampleMapper.map(dataSampleService.list(dataCollection.getId())),
                HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<DataSampleDTO>> listDataSamplesByTemplateName(final String templateName) {
        final List<DataSampleEntity> listByTemplateName = dataSampleService.getListByTemplateName(decodeBase64(templateName));
        return new ResponseEntity<>(dataSampleMapper.map(listByTemplateName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> getDataSample(final String dataCollectionName, final String dataSampleName) {
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleService.get(decodeBase64(dataSampleName))),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteDataSampleList(final String dataCollectionName,
                                                     final List<String> dataSampleNames, final Principal principal) {
        dataSampleService.delete(dataSampleNames);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deleteAllDataSamples(final String dataCollectionName, final Principal principal) {
        dataSampleService.delete(dataCollectionService.get(decodeBase64(dataCollectionName)));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> setDataSampleAsDefault(final String dataCollectionName, final String dataSampleName,
                                                                final Principal principal) {
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleService.setAsDefault(decodeBase64(dataSampleName))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> updateDataSample(final String dataCollectionName, final String dataSampleName,
                                                          @Valid final DataSampleUpdateRequestDTO dataSampleUpdateRequestDTO, final Principal principal) {
        final DataSampleEntity entity = dataSampleService.update(decodeBase64(dataSampleName), dataSampleMapper.map(dataSampleUpdateRequestDTO),
                principal.getName());

        return new ResponseEntity<>(dataSampleMapper.mapWithFile(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSampleDTO> createDataSampleNewVersion(final String dataCollectionName,
                                                                    final @Valid DataSampleCreateRequestDTO dataSampleCreateRequestDTO, final Principal principal) {
        final String dataSampleName = dataSampleCreateRequestDTO.getName();
        final String fileName = dataSampleCreateRequestDTO.getFileName();
        final String data = dataSampleCreateRequestDTO.getSample();
        final String comment = dataSampleCreateRequestDTO.getComment();
        final DataSampleEntity dataSampleEntity = dataSampleService.createNewVersion(dataSampleName, data, fileName,
                principal.getName(), comment);
        return new ResponseEntity<>(dataSampleMapper.mapWithFile(dataSampleEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Page<FileVersionDTO>> getDataSampleVersions(final Pageable pageable,
                                                                      final String dataCollectionName, final String name, final VersionFilter versionFilter,
                                                                      final String searchParam) {
        final String dataSampleName = decodeBase64(name);
        final Page<FileVersionModel> dataSampleVersionEntities = dataSampleFileService.list(pageable, dataSampleName,
                versionFilter, searchParam);

        return new ResponseEntity<>(fileVersionMapper.map(dataSampleVersionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> rollback(final Principal principal, final String name, final Long version) {
        final DataCollectionEntity result = dataCollectionService.rollbackVersion(decodeBase64(name), version, principal.getName());
        return new ResponseEntity<>(dataCollectionMapper.mapWithFile(result), HttpStatus.OK);
    }
}
