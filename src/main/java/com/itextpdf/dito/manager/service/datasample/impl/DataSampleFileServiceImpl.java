package com.itextpdf.dito.manager.service.datasample.impl;

import com.itextpdf.dito.manager.filter.version.VersionFilter;
import com.itextpdf.dito.manager.model.file.FileVersionModel;
import com.itextpdf.dito.manager.repository.datasample.DataSampleFileRepository;
import com.itextpdf.dito.manager.service.datasample.DataSampleFileService;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import static com.itextpdf.dito.manager.filter.FilterUtils.getEndDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getLongFromFilter;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStartDateFromRange;
import static com.itextpdf.dito.manager.filter.FilterUtils.getStringFromFilter;

@Component
public class DataSampleFileServiceImpl implements DataSampleFileService{
    private DataSampleFileRepository dataSampleFileRepository;

    @Autowired
    public DataSampleFileServiceImpl(final DataSampleFileRepository dataSampleFileRepository) {
        this.dataSampleFileRepository = dataSampleFileRepository;
    }

    @Override
    public Page<FileVersionModel> list(final Pageable pageable, final String name, final VersionFilter filter, final String searchParam) {
        final Long version = getLongFromFilter(filter.getVersion());
        final String createdBy = getStringFromFilter(filter.getModifiedBy());
        final String comment = getStringFromFilter(filter.getComment());
        final String stageName = getStringFromFilter(filter.getStage());

        Date createdOnStartDate = null;
        Date createdOnEndDate = null;
        final List<String> createdOnDateRange = filter.getModifiedOn();
        if (createdOnDateRange != null) {
            if (createdOnDateRange.size() != 2) {
                throw new IllegalArgumentException("Date range should contain two elements: start date and end date");
            }
            createdOnStartDate = getStartDateFromRange(createdOnDateRange);
            createdOnEndDate = getEndDateFromRange(createdOnDateRange);
        }

        return StringUtils.isEmpty(searchParam)
                ? dataSampleFileRepository.filter(pageable, name, version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment)
                : dataSampleFileRepository.search(pageable, name, version, createdBy, createdOnStartDate, createdOnEndDate, stageName, comment, searchParam.toLowerCase());
    }
}