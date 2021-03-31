package com.itextpdf.dito.manager.component.mapper.file;

import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;

public interface FileVersionMapper {
    FileVersionDTO map(FileVersionModel entity);

    Page<FileVersionDTO> map(Page<FileVersionModel> entity);
}
