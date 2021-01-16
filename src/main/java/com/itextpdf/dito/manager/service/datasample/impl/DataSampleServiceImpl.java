package com.itextpdf.dito.manager.service.datasample.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleStructureException;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.user.UserService;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataSampleServiceImpl extends AbstractService implements DataSampleService {

    private final DataSampleRepository dataSampleRepository;
    private final UserService userService;
    private final JsonValidator jsonValidator;
    private final JsonKeyComparator jsonKeyComparator;


	public DataSampleServiceImpl(final DataSampleRepository dataSampleRepository, 
								 final UserService userService,
								 final JsonValidator jsonValidator,
								 final JsonKeyComparator jsonKeyComparator) {
		this.dataSampleRepository = dataSampleRepository;
		this.userService = userService;
		this.jsonValidator = jsonValidator;
		this.jsonKeyComparator = jsonKeyComparator;
	}

	@Override
	public DataSampleEntity create(final DataCollectionEntity dataCollectionEntity, final String name, final String fileName,
			final String sample, final String comment, final String email) {
		if (!jsonValidator.isValid(sample.getBytes())) {
			throw new InvalidDataSampleException();
		}
		final DataCollectionFileEntity lastEntity = dataCollectionEntity.getLatestVersion();
		final String jsonFromCollection = new String(lastEntity.getData());
		if(!jsonKeyComparator.checkJsonKeysEquals(jsonFromCollection, sample)) {
			throw new InvalidDataSampleStructureException();
		}
		
		final UserEntity userEntity = userService.findByEmail(email);

		final DataSampleEntity dataSampleEntity = new DataSampleEntity();
		dataSampleEntity.setDataCollection(dataCollectionEntity);
		dataSampleEntity.setName(name);
		dataSampleEntity.setComment(comment);
		dataSampleEntity.setFileName(fileName);
		dataSampleEntity.setModifiedOn(new Date());
		dataSampleEntity.setCreatedOn(new Date());
		dataSampleEntity.setAuthor(userEntity);
		dataSampleEntity.setData(sample.getBytes());

		return dataSampleRepository.save(dataSampleEntity);
	}	

	@Override
	protected List<String> getSupportedSortFields() {
		// TODO Auto-generated method stub
		return null;
	}
}
