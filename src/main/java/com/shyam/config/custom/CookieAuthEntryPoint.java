package com.shyam.config.custom;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.shyam.exceptions.AuthorizationMissingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieAuthEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @SuppressWarnings("null")
    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                System.out.println("in Cookie auth entry point");
                throw new AuthorizationMissingException("Didn't receive any cookies with the request", request.getServletPath());
            }
                
            String token = null;
            for (Cookie cookie : cookies) 
                if ("jwtToken".equals(cookie.getName())) 
                    token = cookie.getValue();
                
            if (token == null) 
                throw new AuthorizationMissingException("Didn't receive any cookies with key as 'jwtToken' from the request request", request.getServletPath());

        } catch (AuthorizationMissingException e) {
            // e.printStackTrace();
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
        
    }
    
}
