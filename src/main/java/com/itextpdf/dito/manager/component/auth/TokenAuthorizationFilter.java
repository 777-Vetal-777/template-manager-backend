package com.itextpdf.dito.manager.component.auth;

import com.itextpdf.dito.manager.component.auth.token.extractor.TokenExtractor;
import com.itextpdf.dito.manager.component.auth.token.helper.TokenHelper;

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
    private static final Logger log = LogManager.getLogger(TokenAuthorizationFilter.class);

    private final TokenExtractor tokenExtractor;
    private final TokenHelper tokenManager;
    private final UserDetailsService userDetailsService;

    public TokenAuthorizationFilter(final TokenExtractor tokenExtractor,
            final TokenHelper tokenManager,
            final UserDetailsService userDetailsService) {
        this.tokenExtractor = tokenExtractor;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = tokenExtractor.extract(httpServletRequest);
            if (!StringUtils.isEmpty(token) && tokenManager.isValid(token)) {
                final String username = tokenManager.getSubject(token);

                if (!StringUtils.isEmpty(username)) {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception ex) {
            log.error("Unable to set user authentication -> ", ex);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
