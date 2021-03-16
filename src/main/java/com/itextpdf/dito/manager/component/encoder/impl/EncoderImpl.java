package com.itextpdf.dito.manager.component.encoder.impl;

import com.itextpdf.dito.manager.component.encoder.Encoder;
import com.itextpdf.dito.manager.exception.Base64DecodeException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncoderImpl implements Encoder {

    @Override
    public String encode(String name) {
        return Base64.getUrlEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decode(String data) {
        try {
            return new String(Base64.getUrlDecoder().decode(data), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new Base64DecodeException(data);
        }
    }
}
