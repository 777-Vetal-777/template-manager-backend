package com.itextpdf.dito.manager.component.auth;

import com.itextpdf.dito.manager.component.auth.token.TokenManager;
import com.itextpdf.dito.manager.component.auth.token.impl.JwtManagerImpl;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger log = LogManager.getLogger(JwtManagerImpl.class);

    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;

    public TokenAuthorizationFilter(TokenManager tokenManager,
            UserDetailsService userDetailsService) {
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = retrieveToken(request);
            if (!StringUtils.isEmpty(token) && tokenManager.validate(token)) {
                String username = tokenManager.getSubject(token);

                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception ex) {
            log.error("Unable to set user authentication -> ", ex);
        }
        filterChain.doFilter(request, response);
    }

    private String retrieveToken(HttpServletRequest request) {
        String result = null;

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            result = authHeader.replace("Bearer ", "");
        } else {
            log.error("Incorrect token format: token must not be NULL and should start with 'Bearer '");
        }

        return result;
    }
}
