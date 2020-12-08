package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.exception.sort.UnsupportedSortFieldException;

import java.util.List;
import org.springframework.data.domain.Sort;

public abstract class AbstractService {

    protected abstract List<String> getSupportedSortFields();

    protected void throwExceptionIfSortedFieldIsNotSupported(final Sort sort) {
        sort.forEach(sortedField -> {
            if (!getSupportedSortFields().contains(sortedField.getProperty())) {
                throw new UnsupportedSortFieldException(buildUnsupportedSortedFieldMessage(sortedField.getProperty()));

            }
        });
    }

    private String buildUnsupportedSortedFieldMessage(final String sortedField) {
        final StringBuilder result = new StringBuilder("sorting by field ");
        result.append(sortedField);
        result.append(" is not supported");
        return result.toString();
    }
}
