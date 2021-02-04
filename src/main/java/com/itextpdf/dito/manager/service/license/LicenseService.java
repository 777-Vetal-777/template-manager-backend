package com.itextpdf.dito.manager.service.license;

import com.itextpdf.dito.manager.entity.LicenseEntity;
import com.itextpdf.dito.manager.entity.WorkspaceEntity;

public interface LicenseService {
	LicenseEntity uploadLicense(WorkspaceEntity workspaceEntity, byte[] data, String fileName);
}
