package com.itextpdf.dito.manager.component.mapper.license.impl;

import java.io.ByteArrayInputStream;
import java.util.Date;

import com.itextpdf.dito.sdk.aws.LicenseServerWrapper;
import com.itextpdf.dito.sdk.event.DitoEventType;
import com.itextpdf.dito.sdk.license.DitoLicense;
import com.itextpdf.dito.sdk.license.DitoLicenseInfo;

public class DitoLicenseInfoHelper {
	final DitoLicense ditoLicense;
	final LicenseServerWrapper serverWrapper;
	final DitoLicenseInfo ditoLicenseInfo;

	public DitoLicenseInfoHelper(final byte[] data) {
		ditoLicense = DitoLicense.parseLicense(new ByteArrayInputStream(data));
		serverWrapper = LicenseServerWrapper.create(ditoLicense);
		ditoLicenseInfo = ditoLicense.getInfo();
	}

	public String getType() {
		return ditoLicenseInfo.getType();
	}

	public Date getExpirationDate() {
		return ditoLicenseInfo.parseExpire();
	}

	public String getLimits() {
		return ditoLicenseInfo.getLimits().getLimit(DitoEventType.PRODUCE);
	}

	public Long getRemainingEvents() {
		return serverWrapper.getRemainingPdfProduceEvents();
	}

}
