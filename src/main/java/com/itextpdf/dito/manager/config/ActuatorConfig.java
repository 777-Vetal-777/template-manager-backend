package com.itextpdf.dito.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ActuatorConfig {

    @Bean
    InfoContributor licenseContributor(final ObjectMapper objectMapper) {
        final Logger log = LogManager.getLogger(InfoContributor.class);

        return builder -> {
            try {
                final Map<String, Object> nodes = objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("license-mapping.json"), objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
                builder.withDetails(nodes);
            } catch (IOException e) {
                log.error("Error retrieving license information: ".concat(e.getMessage()));
            }
        };
    }

}
