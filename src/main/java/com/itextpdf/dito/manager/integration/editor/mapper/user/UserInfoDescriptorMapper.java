package com.itextpdf.dito.manager.integration.editor.mapper.user;

import com.itextpdf.dito.editor.server.common.core.descriptor.UserInfoDescriptor;
import com.itextpdf.dito.manager.entity.UserEntity;

public interface UserInfoDescriptorMapper {
	UserInfoDescriptor map(UserEntity userEntity);
}
