package com.itextpdf.dito.manager.controller.datacollection.impl;

import com.itextpdf.dito.manager.component.mapper.datacollection.DataCollectionMapper;
import com.itextpdf.dito.manager.component.mapper.role.RoleMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.datacollection.DataCollectionController;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionType;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionVersionDTO;
import com.itextpdf.dito.manager.dto.datacollection.update.DataCollectionUpdateRequestDTO;
import com.itextpdf.dito.manager.dto.dependency.DependencyDTO;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.update.ApplyRoleRequestDTO;
import com.itextpdf.dito.manager.dto.role.RoleDTO;
import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.resource.ResourceEntity;
import com.itextpdf.dito.manager.exception.datacollection.EmptyDataCollectionFileException;
import com.itextpdf.dito.manager.exception.datacollection.NoSuchDataCollectionTypeException;
import com.itextpdf.dito.manager.exception.datacollection.UnreadableDataCollectionException;
import com.itextpdf.dito.manager.filter.datacollection.DataCollectionFilter;
import com.itextpdf.dito.manager.filter.role.RoleFilter;
import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionFileService;
import com.itextpdf.dito.manager.service.datacollection.DataCollectionService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@RestController
public class DataCollectionControllerImpl extends AbstractController implements DataCollectionController {
    private final DataCollectionService dataCollectionService;
    private final DataCollectionMapper dataCollectionMapper;
    private final DataCollectionFileService dataCollectionFileService;
    private final RoleMapper roleMapper;

    public DataCollectionControllerImpl(final DataCollectionService dataCollectionService,
                                        final DataCollectionMapper dataCollectionMapper,
                                        final RoleMapper roleMapper,
                                        final DataCollectionFileService dataCollectionFileService) {
        this.dataCollectionService = dataCollectionService;
        this.dataCollectionMapper = dataCollectionMapper;
        this.roleMapper = roleMapper;
        this.dataCollectionFileService = dataCollectionFileService;
    }

    @Override
    public ResponseEntity<DataCollectionDTO> create(final String name, final String dataCollectionType, final MultipartFile multipartFile, final Principal principal) {
        if (multipartFile.isEmpty()) {
            throw new EmptyDataCollectionFileException();
        }
        if (!EnumUtils.isValidEnum(DataCollectionType.class, dataCollectionType)) {
            throw new NoSuchDataCollectionTypeException(dataCollectionType);
        }
        final DataCollectionType collectionType = DataCollectionType.valueOf(dataCollectionType);

        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new UnreadableDataCollectionException(multipartFile.getOriginalFilename());
        }
        final DataCollectionEntity dataCollectionEntity = dataCollectionService
                .create(name, collectionType, data, multipartFile.getOriginalFilename(), principal.getName());
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<DependencyDTO> list(String name) {
        return null;
    }

    @Override
    public ResponseEntity<ResourceDTO> create(Principal principal, String name, String dataCollectionType, MultipartFile multipartFile, String comment) {
        return null;
    }

    @Override
    public ResponseEntity<Page<DataCollectionDTO>> list(final Pageable pageable, final DataCollectionFilter filter,
                                                        final String searchParam) {

        final Page<DataCollectionEntity> dataCollectionEntities = dataCollectionService.list(pageable, filter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> get(final String name) {
        return new ResponseEntity<>(dataCollectionMapper.map(dataCollectionService.get(decodeBase64(name))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataCollectionDTO> update(final String name,
                                                    final @Valid DataCollectionUpdateRequestDTO dataCollectionUpdateRequestDTO,
                                                    final Principal principal) {
        final DataCollectionEntity entity = dataCollectionService
                .update(decodeBase64(name), dataCollectionMapper.map(dataCollectionUpdateRequestDTO),
                        principal.getName());

        return new ResponseEntity<>(dataCollectionMapper.map(entity), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(final String name) {
        dataCollectionService.delete(decodeBase64(name));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<DataCollectionVersionDTO>> getVersions(final Pageable pageable,
                                                                      final String name,
                                                                      final VersionFilter versionFilter,
                                                                      final String searchParam) {
        final String dataCollectionName = decodeBase64(name);
        final Page<DataCollectionFileEntity> dataCollectionVersionEntities = dataCollectionFileService.list(pageable, dataCollectionName, versionFilter, searchParam);
        return new ResponseEntity<>(dataCollectionMapper.mapVersions(dataCollectionVersionEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Page<RoleDTO>> getRoles(final Pageable pageable, final String name, final RoleFilter filter) {
        final Page<RoleEntity> roleEntities = dataCollectionService
                .getRoles(pageable, decodeBase64(name), filter);
        return new ResponseEntity<>(roleMapper.map(roleEntities), HttpStatus.OK);
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
}
