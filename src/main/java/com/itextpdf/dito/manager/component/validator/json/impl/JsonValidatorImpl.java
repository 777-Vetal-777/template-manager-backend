package com.itextpdf.dito.manager.component.validator.json.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.dito.manager.component.validator.json.JsonValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonValidatorImpl implements JsonValidator {
    private static final Logger log = LogManager.getLogger(JsonValidatorImpl.class);
    private final ObjectMapper objectMapper;

    public JsonValidatorImpl() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    }

    @Override
    public boolean isValid(byte[] data) {
        boolean result = true;

        try {
            objectMapper.readTree(data);
        } catch (IOException e) {
            result = false;
        }
        log.info("Validate was finished successfully");
        return result;
    }
}
