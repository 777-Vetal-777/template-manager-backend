package com.itextpdf.dito.manager.component.validator.resource;

import com.itextpdf.dito.manager.dto.resource.ResourceTypeEnum;

public interface ContentTypeDetector {

    ResourceTypeEnum detectType(byte[] data);

}
