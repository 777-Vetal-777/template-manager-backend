package com.itextpdf.dito.manager.component.mapper.license.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.itextpdf.dito.manager.component.mapper.license.LicenseMapper;
import com.itextpdf.dito.manager.dto.license.LicenseDTO;
import com.itextpdf.dito.manager.entity.LicenseEntity;
import com.itextpdf.dito.sdk.aws.LicenseServerWrapper;
import com.itextpdf.dito.sdk.event.DitoEventType;
import com.itextpdf.dito.sdk.license.DitoLicense;
import com.itextpdf.dito.sdk.license.DitoLicenseInfo;

@Component
public class LicenseMapperImpl implements LicenseMapper{
	
	@Override
	public LicenseDTO map(final LicenseEntity entity) {
		final LicenseDTO dto = new LicenseDTO();
		final DitoLicense ditoLicense = DitoLicense.parseLicense(new ByteArrayInputStream(entity.getData()));
		final LicenseServerWrapper serverWrapper = LicenseServerWrapper.create(ditoLicense);
		final DitoLicenseInfo ditoLicenseInfo = ditoLicense.getInfo();
		dto.setFileName(entity.getFileName());
		dto.setType(ditoLicenseInfo.getType());
		dto.setExpirationDate(ditoLicenseInfo.parseExpire());
		if (StringUtils.isNumeric(ditoLicenseInfo.getLimits().getLimit(DitoEventType.PRODUCE))) {
			dto.setVolumeLimit(Long.parseLong(ditoLicenseInfo.getLimits().getLimit(DitoEventType.PRODUCE)));
			dto.setVolumeUsed(dto.getVolumeLimit() - serverWrapper.getRemainingPdfProduceEvents());
			dto.setIsUnlimited(false);
		} else {
			dto.setVolumeUsed(0L);
			dto.setVolumeLimit(0L);
			dto.setIsUnlimited(true);
		}

		return dto;
	}

}
