package com.itextpdf.dito.manager.controller.resource.impl;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.controller.resource.ResourceController;
import com.itextpdf.dito.manager.dto.resource.ResourceDTO;
import com.itextpdf.dito.manager.dto.resource.filter.ResourceFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class ResourceControllerImpl extends AbstractController implements ResourceController {

    @Override
    public ResponseEntity<Page<ResourceDTO>> list(final Pageable pageable,
                                                  final ResourceFilterDTO filter,
                                                  final String searchParam) {
        //TODO implement logic within https://jira.itextsupport.com/browse/DTM-472
        final ResourceDTO mockedResource = new ResourceDTO();
        Page<ResourceDTO> result = new PageImpl<>(Collections.singletonList(mockedResource));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResourceDTO> get(final String name) {
        String decodedResourceName = decodeBase64(name);
        //TODO implement logic within https://jira.itextsupport.com/browse/DTM-472
        final ResourceDTO result = new ResourceDTO();
        result.setName(decodedResourceName);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
