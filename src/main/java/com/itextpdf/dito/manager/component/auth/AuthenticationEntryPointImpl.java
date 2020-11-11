package com.itextpdf.dito.manager.component.auth;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(final HttpServletRequest pRequest, final HttpServletResponse pResponse,
            final AuthenticationException pAuthException) throws IOException {
        pResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

