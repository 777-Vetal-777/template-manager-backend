package com.itextpdf.dito.manager.service;

import com.itextpdf.dito.manager.dto.UserCreateRequest;
import com.itextpdf.dito.manager.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ManagerMapper {
    User fromRequest(UserCreateRequest request);
}
