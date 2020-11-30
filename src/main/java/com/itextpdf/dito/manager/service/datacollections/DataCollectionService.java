package com.itextpdf.dito.manager.service.datacollections;

import com.itextpdf.dito.manager.dto.datacollection.DataCollectionCreateRequestDTO;
import com.itextpdf.dito.manager.dto.datacollection.DataCollectionDTO;
import com.itextpdf.dito.manager.entity.DataCollectionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface DataCollectionService {
    DataCollectionDTO create(DataCollectionCreateRequestDTO requestDTO, MultipartFile attachment, Principal principal);

    Page<DataCollectionEntity> list(Pageable pageable, String searchParam);
}
