package com.itextpdf.dito.manager.component.mapper.datasample.impl;

import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DataSampleMapperImpl implements DataSampleMapper {

	@Override
	public DataSampleDTO map(final DataSampleEntity entity) {
		final DataSampleDTO dto = new DataSampleDTO();
		final UserEntity modifiedBy = entity.getAuthor();
        dto.setName(entity.getName());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setComment(entity.getComment());
        dto.setFileName(entity.getFileName());
        return dto;
	}

    @Override
    public Page<DataSampleDTO> map(final Page<DataSampleEntity> entities) {
        return entities.map(this::map);
    }
}
