package com.itextpdf.dito.manager.component.client.instance.impl;

import com.itextpdf.dito.manager.component.client.instance.InstanceClient;
import com.itextpdf.dito.manager.component.client.instance.InstanceHealthChecker;

import com.itextpdf.dito.manager.dto.instance.register.InstanceRegisterResponseDTO;
import com.itextpdf.dito.manager.entity.InstanceEntity;
import com.itextpdf.dito.manager.repository.instance.InstanceRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanceHealthCheckerImpl implements InstanceHealthChecker {

    private final InstanceClient instanceClient;
    private final InstanceRepository instanceRepository;

    public InstanceHealthCheckerImpl(final InstanceClient instanceClient, final InstanceRepository instanceRepository) {
        this.instanceClient = instanceClient;
        this.instanceRepository = instanceRepository;
    }

    @Override
    public void check() {
        final List<InstanceEntity> allInstances = instanceRepository.findAll();
        final List<InstanceEntity> failedRegisteredInstances = allInstances.stream()
                .map(instance -> {
                    instance.setActive(instanceClient.checkIsInstanceAlreadyRegistered(instance.getSocket(), instance.getHeaderName(), instance.getHeaderValue(), instance.getRegisterToken()));
                    return instance;
                })
                .collect(Collectors.toList());
        for (InstanceEntity instance : failedRegisteredInstances) {
            if (Boolean.FALSE.equals(instance.getActive())) {
                try {
                    final InstanceRegisterResponseDTO response = instanceClient.register(instance.getSocket(), instance.getHeaderName(), instance.getHeaderValue());
                    instance.setRegisterToken(response.getToken());
                    instance.setActive(true);
                } catch (RuntimeException e) {
                    instance.setActive(false);
                }
            }
        }
        instanceRepository.saveAll(failedRegisteredInstances);
    }

    @PostConstruct
    public void checkInstancesHealth() {
        this.check();
    }
}
