package com.itextpdf.dito.manager.service.scheduler;

import com.itextpdf.dito.manager.repository.template.TemplateRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

@Component
public class TemplateUnblockScheduler {

    private final TemplateRepository templateRepository;

    public TemplateUnblockScheduler(final TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Scheduled(
            fixedDelayString = "${template.unblock-scheduler.execution-delay-in-millis}",
            initialDelayString = "${template.unblock-scheduler.initial-delay-in-millis}"
    )
    @Transactional
    public void unlockTemplatesWithExpiredBlockTime() {
        final int blockExpirationInHours = 12;
        Date expirationDate = DateUtils.addHours(new Date(), -blockExpirationInHours);
        templateRepository.unlockTemplatesWithExpiredBlockTime(expirationDate);
    }
}
