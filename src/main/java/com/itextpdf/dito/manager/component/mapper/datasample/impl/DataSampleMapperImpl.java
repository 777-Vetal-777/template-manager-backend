package com.itextpdf.dito.manager.component.mapper.datasample.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DataSampleMapperImpl implements DataSampleMapper {

	private final JsonKeyComparator jsonKeyComparator;
	
	
	public DataSampleMapperImpl(JsonKeyComparator jsonKeyComparator) {
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
        dto.setAuthorFirstName(entity.getAuthor().getFirstName());
        dto.setAuthorLastName(entity.getAuthor().getLastName());
        dto.setComment(entity.getComment());
        dto.setFileName(entity.getFileName());
        dto.setSetAsDefault(entity.getSetAsDefault());
		dto.setIsActual(jsonKeyComparator.checkJsonKeysEquals(new String(entity.getData()),
				new String(entity.getDataCollection().getLatestVersion().getData())));
		 return dto;
	}

    @Override
    public DataSampleDTO mapWithFile(final DataSampleEntity entity) {
        final DataSampleDTO result = map(entity);
        result.setFile(new String(entity.getData()));
        return result;
    }

    @Override
    public Page<DataSampleDTO> map(final Page<DataSampleEntity> entities) {
        return entities.map(this::map);
    }
}
