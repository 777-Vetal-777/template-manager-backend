package com.itextpdf.dito.manager.component.mapper.datasample.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.itextpdf.dito.manager.component.mapper.datasample.DataSampleMapper;
import com.itextpdf.dito.manager.dto.datasample.DataSampleDTO;
import com.itextpdf.dito.manager.dto.datasample.update.DataSampleUpdateRequestDTO;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleFileEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataSampleMapperImpl implements DataSampleMapper {

    private static final Logger log = LogManager.getLogger(DataSampleMapperImpl.class);


    @Override
    public DataSampleDTO map(final DataSampleEntity entity) {
        log.info("Convert dataSample: {} to dto was started", entity.getId());
        final DataSampleDTO dto = new DataSampleDTO();
        final UserEntity modifiedBy = entity.getModifiedBy();
        dto.setName(entity.getName());
        dto.setModifiedBy(new StringBuilder(modifiedBy.getFirstName()).append(" ").append(modifiedBy.getLastName()).toString());
        dto.setModifiedOn(entity.getModifiedOn());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setCreatedBy(new StringBuilder(entity.getAuthor().getFirstName()).append(" ")
                .append(entity.getAuthor().getLastName()).toString());
        dto.setDescription(entity.getDescription());
        dto.setFileName(entity.getLatestVersion().getFileName());
        dto.setIsDefault(entity.getIsDefault());
        dto.setComment(entity.getLatestVersion().getComment());
        dto.setVersion(entity.getLatestVersion().getVersion());
        dto.setIsActual(checkJsonsEquality(new String(entity.getDataCollection().getLatestVersion().getData(), StandardCharsets.UTF_8),
                new String(entity.getLatestVersion().getData(), StandardCharsets.UTF_8)));
        log.info("Convert dataSample: {} to dto was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public DataSampleDTO mapWithFile(final DataSampleEntity entity) {
        log.info("Convert dataSample: {} to dto was started", entity.getId());
        final DataSampleDTO dto = map(entity);
        final DataSampleFileEntity latestVersion = entity.getLatestVersion();
        dto.setFile(new String(latestVersion.getData(), StandardCharsets.UTF_8));
        dto.setVersion(latestVersion.getVersion());
        dto.setComment(latestVersion.getComment());
        dto.setFileName(latestVersion.getFileName());
        log.info("Convert dataSample: {} to dto was finished successfully", entity.getId());
        return dto;
    }

    @Override
    public Page<DataSampleDTO> map(final Page<DataSampleEntity> entities) {
        return entities.map(this::map);
    }

    @Override
    public DataSampleEntity map(final DataSampleUpdateRequestDTO dto) {
        log.info("Convert {} to dataSample was started", dto);
        final DataSampleEntity entity = new DataSampleEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        log.info("Convert {} to dataSample was finished successfully", dto);
        return entity;
    }

    @Override
    public List<DataSampleDTO> map(final List<DataSampleEntity> entities) {
        return entities.stream().map(this::map).collect(Collectors.toList());

    }

    private boolean checkJsonsEquality(final String dataCollectionJson, final String dataSampleJson) {
        final ObjectMapper mapper = new ObjectMapper();
        boolean equals = false;
        try {        	       	
        	final List<String> dataCollectionKeyList = getAllKeys(mapper, dataCollectionJson);
        	final List<String> dataSampleKeyList = getAllKeys(mapper, dataSampleJson);
        
        	dataCollectionKeyList.removeAll(dataSampleKeyList);
        	dataSampleKeyList.retainAll(dataCollectionKeyList);
        	equals =  dataCollectionKeyList.isEmpty() &&  dataSampleKeyList.isEmpty();
        } catch (JsonProcessingException e) {
            log.error("Failed to check jsons equality");
        }
        return equals;
    }

	private List<String> getAllKeys(final ObjectMapper mapper, final String jsonObject) throws JsonProcessingException {
		final Map<String, Object> treeMap = mapper.readValue(jsonObject, Map.class);
		final List<String> keys = Lists.newArrayList();
		return findKeys(treeMap, keys);

	}

	private List<String> findKeys(final Map<String, Object> treeMap, final List<String> keys) {
		treeMap.forEach((key, value) -> {
			if (value instanceof LinkedHashMap) {
				final Map<String, Object> map = (LinkedHashMap) value;
				findKeys(map, keys);
			}
			keys.add(key);
		});

		return keys;
	}
}
