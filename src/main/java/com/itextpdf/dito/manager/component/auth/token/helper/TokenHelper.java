package com.itextpdf.dito.manager.component.auth.token.helper;

import java.util.Date;

public interface TokenHelper {
    boolean isValid(String token);

    String getSubject(String token);

    boolean isTokenWasIssuedAfter(String token, Date date);
}
