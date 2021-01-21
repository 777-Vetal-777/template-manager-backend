package com.itextpdf.dito.manager.service.datasample.impl;

import com.itextpdf.dito.manager.component.datasample.jsoncomparator.JsonKeyComparator;
import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionEntity;
import com.itextpdf.dito.manager.entity.datacollection.DataCollectionFileEntity;
import com.itextpdf.dito.manager.entity.datasample.DataSampleEntity;
import com.itextpdf.dito.manager.exception.datacollection.DataCollectionNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleAlreadyExistsException;
import com.itextpdf.dito.manager.exception.datasample.DataSampleNotFoundException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleException;
import com.itextpdf.dito.manager.exception.datasample.InvalidDataSampleStructureException;
import com.itextpdf.dito.manager.exception.date.InvalidDateRangeException;
import com.itextpdf.dito.manager.filter.datasample.DataSampleFilter;
import com.itextpdf.dito.manager.repository.datasample.DataSampleRepository;
import com.itextpdf.dito.manager.service.AbstractService;
import com.itextpdf.dito.manager.service.datasample.DataSampleService;
import com.itextpdf.dito.manager.service.user.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.dito.manager.filter.FilterUtils.getBooleanMultiselectFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

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
		
		if (dataSampleRepository.existsByName(name)) {
	        throw new DataSampleAlreadyExistsException(name);
	    }
		
		if (!jsonValidator.isValid(sample.getBytes())) {
			throw new InvalidDataSampleException();
		}

		final DataCollectionFileEntity lastEntity = dataCollectionEntity.getLatestVersion();
		final String jsonFromCollection = new String(lastEntity.getData());
		if (!jsonKeyComparator.checkJsonKeysEquals(jsonFromCollection, sample)) {
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
		dataSampleEntity.setIsDefault(!dataSampleRepository.existsByDataCollection(dataCollectionEntity));
		return dataSampleRepository.save(dataSampleEntity);
	}	

	@Override
	protected List<String> getSupportedSortFields() {
		return DataSampleRepository.SUPPORTED_SORT_FIELDS;
	}

	@Override
	public Page<DataSampleEntity> list(final Pageable pageable, final DataSampleFilter filter, final String searchParam) {
		throwExceptionIfSortedFieldIsNotSupported(pageable.getSort());

		final Pageable pageWithSort = updateSort(pageable);
		final String name = getStringFromFilter(filter.getName());
		final String modifiedBy = getStringFromFilter(filter.getModifiedBy());
		final String comment = getStringFromFilter(filter.getComment());
		final Boolean isDefault = getBooleanMultiselectFromFilter(filter.getIsDefault());

		Date editedOnStartDate = null;
		Date editedOnEndDate = null;
		final List<String> editedOnDateRange = filter.getModifiedOn();
		if (editedOnDateRange != null) {
			if (editedOnDateRange.size() != 2) {
				throw new InvalidDateRangeException();
			}
			editedOnStartDate = getStartDateFromRange(editedOnDateRange);
			editedOnEndDate = getEndDateFromRange(editedOnDateRange);
		}

		return StringUtils.isEmpty(searchParam)
				? dataSampleRepository
				.filter(pageWithSort, name, modifiedBy, editedOnStartDate, editedOnEndDate, isDefault, comment)
				: dataSampleRepository
				.search(pageWithSort, name, modifiedBy, editedOnStartDate, editedOnEndDate, comment,  searchParam.toLowerCase());
	}

	@Override
	public DataSampleEntity get(final String dataSampleName) {
		return dataSampleRepository.findByName(dataSampleName).orElseThrow(() -> new DataSampleNotFoundException(dataSampleName));
	}

	private Pageable updateSort(final Pageable pageable) {
		Sort newSort = Sort.by(pageable.getSort().stream()
				.map(sortParam -> {
					if (sortParam.getProperty().equals("modifiedBy")) {
						sortParam = new Sort.Order(sortParam.getDirection(), "author.firstName");
					}
					return sortParam;
				})
				.collect(Collectors.toList()));
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
	}

	@Override
	public DataSampleEntity setAsDefault(final String dataSampleName) {
		final DataSampleEntity dataSampleEntity = get(dataSampleName);
		final DataCollectionEntity dataCollectionEntity = dataSampleEntity.getDataCollection();
		final List<DataSampleEntity> list = dataSampleRepository.findByDataCollection(dataCollectionEntity)
				.orElseThrow(() -> new DataCollectionNotFoundException(dataCollectionEntity.getName()));
		list.stream().forEach(e -> {
			e.setIsDefault(false);
			e.setModifiedOn(new Date());
		});
		dataSampleEntity.setIsDefault(true);
		dataSampleRepository.saveAll(list);
		return dataSampleEntity;
	}
	
	
	
}
