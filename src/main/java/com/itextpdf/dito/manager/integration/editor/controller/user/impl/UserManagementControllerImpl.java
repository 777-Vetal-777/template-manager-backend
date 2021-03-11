package com.itextpdf.dito.manager.integration.editor.controller.user.impl;

import java.security.Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.dito.editor.server.common.core.descriptor.UserInfoDescriptor;
import com.itextpdf.dito.manager.controller.AbstractController;
import com.itextpdf.dito.manager.entity.UserEntity;
import com.itextpdf.dito.manager.integration.editor.controller.user.UserManagementController;
import com.itextpdf.dito.manager.integration.editor.mapper.user.UserInfoDescriptorMapper;
import com.itextpdf.dito.manager.integration.editor.service.user.UserManagementService;

@RestController
public class UserManagementControllerImpl extends AbstractController implements UserManagementController{

	private static final Logger log = LogManager.getLogger(UserManagementControllerImpl.class);
	
	  private final UserManagementService userManagementService;
	  private final UserInfoDescriptorMapper userInfoDescriptorMapper;
	  
	  public UserManagementControllerImpl(final UserManagementService userManagementService, final UserInfoDescriptorMapper userInfoDescriptorMapper) {
		  this.userManagementService = userManagementService;
		  this.userInfoDescriptorMapper = userInfoDescriptorMapper;
	  }
	
	@Override
	public UserInfoDescriptor getCurrentUser(Principal principal) {
		log.info("Request to get current user with id {} received.", principal.getName());
		final UserEntity userEntity = userManagementService.findCurrentUserByEmail(principal.getName());
		log.info("Response to get current user with id {} processed.", principal.getName());
		return userInfoDescriptorMapper.map(userEntity);
	}

}
