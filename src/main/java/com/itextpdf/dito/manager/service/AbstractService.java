package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.entity.RoleEntity;
import com.itextpdf.dito.manager.exception.role.UnableToSetPermissionsException;
import com.itextpdf.dito.manager.exception.sort.UnsupportedSortFieldException;
import org.springframework.data.domain.Sort;

import java.util.List;

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

    protected void checkNotAdminRole(final RoleEntity roleEntity) {
        if ("GLOBAL_ADMINISTRATOR".equals(roleEntity.getName()) || "ADMINISTRATOR".equals(roleEntity.getName())) {
            throw new UnableToSetPermissionsException();
        }
    }

}
