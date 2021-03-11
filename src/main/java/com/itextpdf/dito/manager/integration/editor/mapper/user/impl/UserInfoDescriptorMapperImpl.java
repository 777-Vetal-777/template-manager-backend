package com.itextpdf.dito.manager.integration.editor.mapper.user.impl;

import org.springframework.stereotype.Component;

import com.itextpdf.dito.editor.server.common.core.descriptor.UserInfoDescriptor;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.editor.mapper.user.UserInfoDescriptorMapper;

@Component
public class UserInfoDescriptorMapperImpl implements UserInfoDescriptorMapper{

	@Override
	public UserInfoDescriptor map(final UserEntity userEntity) {
		final UserInfoDescriptor userDescriptor = new UserInfoDescriptor();
		userDescriptor.setFirstName(userEntity.getFirstName());
		userDescriptor.setLastName(userEntity.getLastName());
		userDescriptor.setEmail(userEntity.getEmail());
		return userDescriptor;
	}

}
