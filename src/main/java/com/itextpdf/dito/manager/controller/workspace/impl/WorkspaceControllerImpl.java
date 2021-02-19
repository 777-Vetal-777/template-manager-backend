package com.itextpdf.dito.manager.controller.workspace.impl;

import com.itextpdf.dito.manager.component.mapper.instance.InstanceMapper;
import com.itextpdf.dito.manager.component.mapper.license.LicenseMapper;
import com.itextpdf.dito.manager.component.mapper.workspace.WorkspaceMapper;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.workspace.WorkspaceController;
import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.dto.promotionpath.PromotionPathDTO;
import com.itextpdf.dito.manager.dto.workspace.WorkspaceDTO;
import com.itextpdf.dito.manager.dto.workspace.create.WorkspaceCreateRequestDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.entity.PromotionPathEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.license.EmptyLicenseFileException;
import com.itextpdf.dito.manager.exception.license.UnreadableLicenseException;
import com.itextpdf.dito.manager.service.license.LicenseService;
import com.itextpdf.dito.manager.service.workspace.WorkspaceService;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static com.itextpdf.dito.manager.util.FilesUtils.getFileBytes;

@Validated
@RestController
public class WorkspaceControllerImpl extends AbstractController implements WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;
    private final LicenseService licenseService;
    private final LicenseMapper licenseMapper;
    private final InstanceMapper instanceMapper;

	public WorkspaceControllerImpl(final WorkspaceService workspaceService, final WorkspaceMapper workspaceMapper,
            final LicenseService licenseService, final LicenseMapper licenseMapper, final InstanceMapper instanceMapper) {
		this.workspaceService = workspaceService;
		this.workspaceMapper = workspaceMapper;
		this.licenseService = licenseService;
		this.licenseMapper = licenseMapper;
        this.instanceMapper = instanceMapper;
    }

    @Override
    public ResponseEntity<Boolean> checkIsWorkspaceWithNameExist(final String name) {
        return new ResponseEntity<>(workspaceService.checkIsWorkspaceWithNameExist(name), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Boolean> checkLicenseValidity(final MultipartFile licence, final Principal principal) {
        final Boolean isLicenseValid = licenseService.verifyLicense(getFileBytes(licence));
        return new ResponseEntity<>(isLicenseValid, HttpStatus.OK);
    }

    //TODO Remove the workspace parameter after support for multiple workspaces is implemented.
    @Override
    public ResponseEntity<WorkspaceDTO> create(final WorkspaceCreateRequestDTO workspaceCreateRequestDTO, final MultipartFile file, final Principal principal) {
        final byte[] licenseFile = getBytesFromMultipart(file);
        final List<InstanceEntity> entities = instanceMapper.map(workspaceCreateRequestDTO.getInstances());
        final WorkspaceEntity workspaceEntity = workspaceService.create(workspaceMapper.map(workspaceCreateRequestDTO),licenseFile, entities, file.getOriginalFilename(), principal.getName(), workspaceCreateRequestDTO.getMainDevelopInstance());
        return new ResponseEntity<>(workspaceMapper.map(workspaceEntity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<WorkspaceDTO>> getAll() {
        final List<WorkspaceEntity> workspaceEntities = workspaceService.getAll();
        return new ResponseEntity<>(workspaceMapper.map(workspaceEntities), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> get(final String name) {
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.get(decodeBase64(name))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WorkspaceDTO> update(final String name, final WorkspaceDTO workspaceDTO) {
        WorkspaceEntity workspaceEntity = workspaceMapper.map(workspaceDTO);
        return new ResponseEntity<>(workspaceMapper.map(workspaceService.update(decodeBase64(name), workspaceEntity)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PromotionPathDTO> getPromotionPath(final String workspaceName) {
        final PromotionPathEntity promotionPathEntity = workspaceService.getPromotionPath(decodeBase64(workspaceName));
        final PromotionPathDTO promotionPathDTO = workspaceMapper.map(promotionPathEntity);
        return new ResponseEntity<>(promotionPathDTO, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PromotionPathDTO> updatePromotionPath(final String workspaceName, final PromotionPathDTO promotionPathDTO) {
        final PromotionPathEntity promotionPathEntity = workspaceService.updatePromotionPath(decodeBase64(workspaceName), workspaceMapper.map(promotionPathDTO));
        final PromotionPathDTO result = workspaceMapper.map(promotionPathEntity);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getStageNames(final String workspaceName) {
        final List<String> stageNames = workspaceService.getStageNames(decodeBase64(workspaceName));
        return new ResponseEntity<>(stageNames, HttpStatus.OK);
    }

	@Override
	public ResponseEntity<LicenseDTO> uploadLicense(final String workspaceName, final MultipartFile multipartFile, final Principal principal) {
		final WorkspaceEntity workspaceEntity = workspaceService.get(decodeBase64(workspaceName));
		final byte[] data = getBytesFromMultipart(multipartFile);
		final String fileName = multipartFile.getOriginalFilename();
		return new ResponseEntity<>(licenseMapper.map(licenseService.uploadLicense(workspaceEntity, data, fileName)),
				HttpStatus.OK);
	}

	@Override
	public ResponseEntity<LicenseDTO> getLicense(final String workspaceName, final Principal principal) {
		final WorkspaceEntity workspaceEntity = workspaceService.get(decodeBase64(workspaceName));
		return new ResponseEntity<>(licenseMapper.map(licenseService.getWorkspaceLicense(workspaceEntity)),
				HttpStatus.OK);
	}
	
	@Override
	protected RuntimeException throwEmptyFileException() {
		throw new EmptyLicenseFileException();
	}

	@Override
	protected RuntimeException throwUnreadableFileException(final String fileName) {
		throw new UnreadableLicenseException(fileName);
	}
}
