package com.itextpdf.dito.manager.service.scheduler;

import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

@Component
public class TemplateUnblockScheduler {

    //1 hour
    final long executionDelayInMilliseconds = 3600000;
    final long initialDelayInMilliseconds = 1000;

    private final TemplateRepository templateRepository;

    public TemplateUnblockScheduler(final TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Scheduled(
            fixedDelay = executionDelayInMilliseconds,
            initialDelay = initialDelayInMilliseconds
    )
    @Transactional
    public void unlockTemplatesWithExpiredBlockTime() {
        final int blockExpirationInHours = 12;
        Date expirationDate = DateUtils.addHours(new Date(), -blockExpirationInHours);
        templateRepository.unlockTemplatesWithExpiredBlockTime(expirationDate);
    }
}
