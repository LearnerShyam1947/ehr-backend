package com.shyam.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.shyam.config.custom.AuthEntryPoint;
import com.shyam.config.custom.MyAccessDeniedHandler;
import com.shyam.filters.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPoint authEntryPoint;

    // private final JwtCookieFilter jwtCookieFilter;
    // private final CookieAuthEntryPoint cookieAuthEntryPoint;

    private final MyAccessDeniedHandler accessDeniedHandler;

    @Value("${application.cors.allowedMethods}")
    private List<String> allowedMethods;
    
    @Value("${application.cors.allowedOrigins}")
    private List<String> allowedOrigins;

    @Value("${application.cors.allowedHeaders}")
    private List<String> allowedHeaders;

    @Value("${application.cors.allowCredentials}")
    private boolean allowCredentials;


    private String[] ALLOWED_AUTH_URLS = {
        // -- Swagger UI v2
        "/v2/api-docs",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",

        // -- Swagger UI v3 (OpenAPI)
        "/v3/**",
        "/swagger-ui/**",

        "/api/v1/auth/**",
        "/api/v1/user-application/apply/**",
        "/auth/**",
        "/login/**",
        "/health"
    };

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        security.csrf(
            csrf -> csrf.disable()
        );

        security.cors(
            cors -> cors.configurationSource(corsConfigurationSource())
        );

        security.authorizeHttpRequests(
            authorizer -> authorizer
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                                .requestMatchers(ALLOWED_AUTH_URLS).permitAll()
                                .anyRequest().permitAll()
        );

        security.addFilterBefore(
            jwtAuthFilter,
            UsernamePasswordAuthenticationFilter.class
        );

        security.exceptionHandling(
            exception -> exception
                            .accessDeniedHandler(accessDeniedHandler)
                            .authenticationEntryPoint(authEntryPoint)
        );

        security.sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return security.build();
    }
    
}
