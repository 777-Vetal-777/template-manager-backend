package com.itextpdf.dito.manager.component.mapper.license.impl;

import java.io.ByteArrayInputStream;

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
		dto.setVolumeLeft(serverWrapper.getRemainingPdfProduceEvents());
		dto.setVolumeLimit(ditoLicenseInfo.getLimits().getLimit(DitoEventType.PRODUCE));
		return dto;
	}

}
