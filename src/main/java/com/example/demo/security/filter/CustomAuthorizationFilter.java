package com.example.demo.security.filter;

import com.example.demo.security.JWTUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//the authorization filter that check if the token is valid and has the authority to access the api
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final JWTUtility jwtUtility;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/login") || request.getServletPath().equals("/api/v1/token/refresh")){
             filterChain.doFilter(request, response);
        }else {
            String authorizationHeader = request.getHeader( AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                try {
                    String token = jwtUtility.getToken(authorizationHeader);
                    UsernamePasswordAuthenticationToken authToken = jwtUtility.setUserInfo(token);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                }catch (Exception e){
                    log.error("error loggin in {}", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setStatus( FORBIDDEN.value());
                    Map<String, String> error = new HashMap<>();
                    response.setContentType(APPLICATION_JSON_VALUE);
                    error.put("error_message", e.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }else {
                filterChain.doFilter(request, response );
            }
        }
    }
}
