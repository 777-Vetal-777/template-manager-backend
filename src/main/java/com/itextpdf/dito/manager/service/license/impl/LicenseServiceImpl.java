package com.itextpdf.dito.manager.service.license.impl;

import java.io.ByteArrayInputStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.dito.manager.entity.LicenseEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;
import com.itextpdf.dito.manager.exception.license.InvalidLicenseException;
import com.itextpdf.dito.manager.exception.license.LicenseNotFoundException;
import com.itextpdf.dito.manager.repository.license.LicenseRepository;
import com.itextpdf.dito.manager.repository.workspace.WorkspaceRepository;
import com.itextpdf.dito.manager.service.license.LicenseService;
import com.itextpdf.dito.sdk.license.DitoLicense;
import com.itextpdf.dito.sdk.license.DitoLicenseException;

@Component
public class LicenseServiceImpl implements LicenseService {
	private LicenseRepository licenseRepository;
	private WorkspaceRepository workspaceRepository;

	@Autowired
	public LicenseServiceImpl(final LicenseRepository licenseRepository,
			final WorkspaceRepository workspaceRepository) {
		this.licenseRepository = licenseRepository;
		this.workspaceRepository = workspaceRepository;
	}

	@Override
	@Transactional
	public LicenseEntity uploadLicense(final WorkspaceEntity workspaceEntity, final byte[] data, final String fileName) {
		try {
			checkDitoLicense(data);
		} catch (DitoLicenseException e) {
			throw new InvalidLicenseException();
		}
		if (workspaceEntity.getLicenseEntity() != null) {
			final LicenseEntity entityToDelete = workspaceEntity.getLicenseEntity();
			workspaceEntity.setLicenseEntity(null);
			workspaceRepository.save(workspaceEntity);
			licenseRepository.delete(entityToDelete);
		}
		final LicenseEntity entity = new LicenseEntity();
		entity.setData(data);
		entity.setFileName(fileName);
		entity.setWorkspace(workspaceEntity);
		return licenseRepository.save(entity);

	}

	public void checkDitoLicense(final byte[] data) {
		DitoLicense.parseLicense(new ByteArrayInputStream(data));
	}

	@Override
	public LicenseEntity getWorkspaceLicense(WorkspaceEntity workspaceEntity) {
		final LicenseEntity entity = licenseRepository.findByWorkspace(workspaceEntity)
				.orElseThrow(LicenseNotFoundException::new);
		try {
			checkDitoLicense(entity.getData());
		} catch (DitoLicenseException e) {
			throw new InvalidLicenseException();
		}
		return entity;
	}

	@Override
	public Boolean verifyLicense(final byte[] data) {
		Boolean result = true;
		try {
			checkDitoLicense(data);
		} catch (DitoLicenseException e) {
			result = false;
		}
		return result;
	}
}
