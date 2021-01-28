package com.itextpdf.dito.manager.component.mapper.datasample.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataSampleMapperImpl implements DataSampleMapper {

	private final JsonKeyComparator jsonKeyComparator;
	
	public DataSampleMapperImpl(final JsonKeyComparator jsonKeyComparator) {
		this.jsonKeyComparator = jsonKeyComparator;
	}

	@Override
	public DataSampleDTO map(final DataSampleEntity entity) {
		final DataSampleDTO dto = new DataSampleDTO();
		final UserEntity modifiedBy = entity.getAuthor();
        dto.setName(entity.getName());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setDescription(entity.getDescription());
        dto.setFileName(entity.getLatestVersion().getFileName());
        dto.setIsDefault(entity.getIsDefault());
        dto.setComment(entity.getLatestVersion().getComment());
        dto.setVersion(entity.getLatestVersion().getVersion());
		dto.setIsActual(jsonKeyComparator.checkJsonKeysEquals(new String(entity.getLatestVersion().getData()),
                new String(entity.getDataCollection().getLatestVersion().getData())));
		 return dto;
	}
 
    @Override
    public DataSampleDTO mapWithFile(final DataSampleEntity entity) {
        final DataSampleDTO dto = map(entity);
        final DataSampleFileEntity latestVersion = entity.getLatestVersion();
        dto.setFile(new String(latestVersion.getData()));
        dto.setVersion(latestVersion.getVersion());
        dto.setComment(latestVersion.getComment());
        dto.setFileName(latestVersion.getFileName());
        return dto;
    }

    @Override
    public Page<DataSampleDTO> map(final Page<DataSampleEntity> entities) {
        return entities.map(this::map);
    }
    
    @Override
    public DataSampleEntity map(DataSampleUpdateRequestDTO dto) {
        final DataSampleEntity entity = new DataSampleEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public List<DataSampleDTO> map(List<DataSampleEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());

    }
}
