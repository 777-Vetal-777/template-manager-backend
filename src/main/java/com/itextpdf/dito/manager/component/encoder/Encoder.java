package com.itextpdf.dito.manager.component.encoder;

public interface Encoder {
    String encode(String name);

    String decode(String encoded);
}
