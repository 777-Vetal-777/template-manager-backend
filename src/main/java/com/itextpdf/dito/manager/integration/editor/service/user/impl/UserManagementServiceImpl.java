package com.itextpdf.dito.manager.integration.editor.service.user.impl;

import org.springframework.stereotype.Service;

import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.editor.service.user.UserManagementService;
import com.itextpdf.dito.manager.service.user.UserService;

@Service
public class UserManagementServiceImpl implements UserManagementService {
	private final UserService userService;
	
	public UserManagementServiceImpl(final UserService userService) {
		this.userService = userService;
	}
	
	@Override
    public UserEntity findCurrentUserByEmail(final String email) {
        return userService.findActiveUserByEmail(email);
    }
}
