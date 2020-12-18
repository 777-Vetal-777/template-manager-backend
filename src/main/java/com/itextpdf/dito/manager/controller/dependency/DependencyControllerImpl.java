package com.itextpdf.dito.manager.controller.dependency;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DependencyControllerImpl extends AbstractController implements DependencyController {
    @Override
    public ResponseEntity<Page<DependencyDTO>> list(final Pageable pageable, final DependencyFilterDTO dependencyFilterDTO, final String searchParam) {
        return null;
    }
}
