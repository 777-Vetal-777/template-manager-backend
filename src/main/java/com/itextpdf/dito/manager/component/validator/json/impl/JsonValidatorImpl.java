package com.itextpdf.dito.manager.component.validator.json.impl;

import com.itextpdf.dito.manager.component.validator.json.JsonValidator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class JsonValidatorImpl implements JsonValidator {
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

        return result;
    }
}
