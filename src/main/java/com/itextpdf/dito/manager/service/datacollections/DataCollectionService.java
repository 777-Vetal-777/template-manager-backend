package com.itextpdf.dito.manager.service.datacollections;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface DataCollectionService {
    DataCollectionDTO create(DataCollectionCreateRequestDTO requestDTO, Principal principal);

    Page<DataCollectionDTO> list(Pageable pageable, String searchParam);
}
