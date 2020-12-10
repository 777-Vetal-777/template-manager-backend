package com.itextpdf.dito.manager.controller.dependency;

import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.dto.dependency.filter.DependencyFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public class DependencyControllerImpl extends AbstractController implements DependencyController{
    @Override
    public ResponseEntity<Page<DependencyDTO>> list(Pageable pageable, DependencyFilterDTO dependencyFilterDTO, String searchParam) {
        return null;
    }
}
