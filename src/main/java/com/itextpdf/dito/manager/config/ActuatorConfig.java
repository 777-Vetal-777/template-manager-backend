package com.itextpdf.dito.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springdoc.core.ActuatorProvider;
import org.springdoc.core.Constants;
import org.springdoc.webmvc.api.WebMvcActuatorProvider;
import org.springframework.boot.actuate.endpoint.web.servlet.ControllerEndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
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

    @Bean
    ActuatorProvider actuatorProvider(WebMvcEndpointHandlerMapping webMvcEndpointHandlerMapping, ControllerEndpointHandlerMapping controllerEndpointHandlerMapping) {
        return new TemplateManagerWebMvcActuatorProvider(webMvcEndpointHandlerMapping, controllerEndpointHandlerMapping);
    }

    static class TemplateManagerWebMvcActuatorProvider extends WebMvcActuatorProvider {

        public TemplateManagerWebMvcActuatorProvider(final WebMvcEndpointHandlerMapping webMvcEndpointHandlerMapping,
                                                     final ControllerEndpointHandlerMapping controllerEndpointHandlerMapping) {
            super(webMvcEndpointHandlerMapping, controllerEndpointHandlerMapping);
        }

        @Override
        public Tag getTag() {
            final Tag actuatorTag = new Tag();
            actuatorTag.setName(Constants.SPRINGDOC_ACTUATOR_TAG.toLowerCase());
            actuatorTag.setDescription(Constants.SPRINGDOC_ACTUATOR_DESCRIPTION);
            actuatorTag.setExternalDocs(
                    new ExternalDocumentation()
                            .url(Constants.SPRINGDOC_ACTUATOR_DOC_URL)
                            .description(Constants.SPRINGDOC_ACTUATOR_DOC_DESCRIPTION)
            );
            return actuatorTag;
        }
    }

}
