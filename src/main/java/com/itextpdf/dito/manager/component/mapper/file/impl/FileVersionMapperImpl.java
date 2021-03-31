package com.itextpdf.dito.manager.component.mapper.file.impl;

import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class FileVersionMapperImpl implements FileVersionMapper {
    private static final Logger log = LogManager.getLogger(FileVersionMapperImpl.class);

    @Override
    public FileVersionDTO map(final FileVersionModel entity) {
        log.info("Convert {} to dto was started", entity);
        final FileVersionDTO fileVersionDTO = new FileVersionDTO();

        fileVersionDTO.setVersion(entity.getVersion());
        fileVersionDTO.setComment(entity.getComment());
        fileVersionDTO.setModifiedBy(entity.getModifiedBy());
        fileVersionDTO.setModifiedOn(entity.getModifiedOn());
        fileVersionDTO.setStage(entity.getStage());
        log.info("Convert {} to dto was finished successfully", entity);
        return fileVersionDTO;
    }

    @Override
    public Page<FileVersionDTO> map(final Page<FileVersionModel> entity) {
        return entity.map(this::map);
    }
}
