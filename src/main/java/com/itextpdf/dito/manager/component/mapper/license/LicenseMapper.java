package com.itextpdf.dito.manager.component.mapper.license;

import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.entity.LicenseEntity;

public interface LicenseMapper {
	LicenseDTO map(LicenseEntity entity);
}
