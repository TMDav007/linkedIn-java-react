package com.linkedIn.backend.features.authentication.filter;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.service.AuthenticationService;
import com.linkedIn.backend.features.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationFilter extends HttpFilter {

    // more like middleware in nodejs
    private final List<String>  unsecuredEndpoints = Arrays.asList(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password"
    );

    private final JsonWebToken jsonWebToken;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(JsonWebToken jsonWebToken, AuthenticationService authenticationService) {
        this.jsonWebToken = jsonWebToken;
        this.authenticationService = authenticationService;
    }

    @Override
    protected  void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
       // response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();

        // if path include the specified unsecured url, it should not check
        if (unsecuredEndpoints.contains(path) || path.startsWith("/api/v1/authentication/oauth")|| path.startsWith("/api/v1/storage")) {
            chain.doFilter(request, response);
            return;
        }

        try{
            String authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new ServletException("Token Missing");
            }
            String token = authorization.substring("Bearer ".length());

            if (jsonWebToken.isTokenExpired(token)) {
                throw new ServletException("Invalid Token");
            }

            String email = jsonWebToken.getEmailFromToken(token);
            AuthenticationUser user = authenticationService.getUser(email);
            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid authentication token. \"}");
        }
    }
}
