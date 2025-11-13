package com.example.expensetracker.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req,
            HttpServletResponse res,
            AuthenticationException ex) throws IOException {

        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"Unauthorized\"}");
    }
}
