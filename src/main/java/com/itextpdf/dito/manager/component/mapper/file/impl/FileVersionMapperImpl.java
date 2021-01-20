package com.itextpdf.dito.manager.component.mapper.file.impl;

import com.itextpdf.dito.manager.component.mapper.file.FileVersionMapper;
import com.itextpdf.dito.manager.dto.file.FileVersionDTO;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FileVersionMapperImpl implements FileVersionMapper {

    @Override
    public FileVersionDTO map(final FileVersionModel entity) {
        final FileVersionDTO fileVersionDTO = new FileVersionDTO();

        fileVersionDTO.setVersion(entity.getVersion());
        fileVersionDTO.setComment(entity.getComment());
        fileVersionDTO.setModifiedBy(entity.getModifiedBy());
        fileVersionDTO.setModifiedOn(entity.getModifiedOn());
        fileVersionDTO.setStage(entity.getStage());

        return fileVersionDTO;
    }

    @Override
    public List<FileVersionDTO> map(final Collection<FileVersionModel> entity) {
        return entity.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public Page<FileVersionDTO> map(final Page<FileVersionModel> entity) {
        return entity.map(this::map);
    }
}
