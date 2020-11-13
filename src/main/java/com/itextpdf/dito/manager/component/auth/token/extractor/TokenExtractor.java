package com.itextpdf.dito.manager.component.auth.token.extractor;

import javax.servlet.http.HttpServletRequest;

public interface TokenExtractor {
    String extract(HttpServletRequest httpServletRequest);
}
