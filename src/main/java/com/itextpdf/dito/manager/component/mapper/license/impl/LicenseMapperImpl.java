package com.itextpdf.dito.manager.component.mapper.license.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.dito.manager.component.mapper.license.LicenseMapper;
import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.entity.LicenseEntity;

@Component
public class LicenseMapperImpl implements LicenseMapper{

	@Override
	public LicenseDTO map(final LicenseEntity entity) {
		final LicenseDTO dto = new LicenseDTO();
		final DitoLicenseInfoHelper ditoHelper = getDitoHelper(entity.getData());
		dto.setFileName(entity.getFileName());
		dto.setType(ditoHelper.getType());
		dto.setExpirationDate(ditoHelper.getExpirationDate());
		if (StringUtils.isNumeric(ditoHelper.getLimits())) {
			dto.setVolumeLimit(Long.parseLong(ditoHelper.getLimits()));
			dto.setVolumeUsed(dto.getVolumeLimit() - ditoHelper.getRemainingEvents());
			dto.setIsUnlimited(false);
		} else {
			dto.setVolumeUsed(0L);
			dto.setVolumeLimit(0L);
			dto.setIsUnlimited(true);
		}

		return dto;
	}

	public DitoLicenseInfoHelper getDitoHelper(final byte[] data) {
		return new DitoLicenseInfoHelper(data);
	}
}
