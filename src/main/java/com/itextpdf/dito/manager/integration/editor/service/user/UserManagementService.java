package com.itextpdf.dito.manager.integration.editor.service.user;

import com.itextpdf.dito.manager.entity.UserEntity;

public interface UserManagementService {
	UserEntity findCurrentUserByEmail(String email);
}
