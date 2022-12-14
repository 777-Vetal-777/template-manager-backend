package com.itextpdf.dito.manager.config;

import com.itextpdf.dito.manager.component.auth.TokenAuthorizationFilter;
import com.itextpdf.dito.manager.controller.feature.OptionController;
import com.itextpdf.dito.manager.controller.login.AuthenticationController;
import com.itextpdf.dito.manager.controller.token.TokenController;

import com.itextpdf.dito.manager.controller.user.UserController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    private static final Logger log = LogManager.getLogger(WebSecurityConfig.class);

    private final TokenAuthorizationFilter tokenAuthorizationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.cors.paths}")
    private String corsPaths;
    @Value("${security.cors.origins}")
    private String corsOrigins;
    @Value("${security.cors.methods}")
    private String corsMethods;

    public WebSecurityConfig(final TokenAuthorizationFilter tokenAuthorizationFilter,
            final AuthenticationEntryPoint authenticationEntryPoint,
            final UserDetailsService userDetailsService,
            final PasswordEncoder passwordEncoder) {
        this.tokenAuthorizationFilter = tokenAuthorizationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String[] SECURITY_WHITELIST = {
            // swagger and api docs
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            // auth endpoint
            AuthenticationController.BASE_NAME,
            UserController.BASE_NAME + UserController.FORGOT_PASSWORD,
            UserController.BASE_NAME + UserController.RESET_PASSWORD,
            TokenController.BASE_NAME + TokenController.REFRESH_ENDPOINT,
            OptionController.BASE_NAME,
            "/actuator/health/ping"
    };

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(SECURITY_WHITELIST)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(tokenAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) {
        try {
            authenticationManagerBuilder
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsPaths)
                .allowedOrigins(corsOrigins)
                .allowedMethods(corsMethods);
    }
}
