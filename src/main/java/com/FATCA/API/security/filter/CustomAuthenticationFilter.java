package com.FATCA.API.security.filter;

import com.FATCA.API.security.JWTUtility;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//the authentication filter is the class that attempt to authenticate the credentials provided and generate a token for the person is trying to log in
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${auth.session.timeout}")
    public long sessionExpiration;
    private final AuthenticationManager authenticationManager;
    private final JWTUtility jwtUtility;
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtility jwtUtility){
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Map<String, String> tokens =  jwtUtility.generateTokens(user, request.getRequestURL().toString());
        System.out.println(Arrays.toString(user.getAuthorities().toArray()));
        var role = user.getAuthorities().toArray();
       tokens.put("username", request.getParameter("username")) ;
       tokens.put("role", role[0].toString());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("usename is {}", userName);
        log.info("password is {}", password);
        String token = null;
        String dateTimeToken = null;

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
        return authenticationManager.authenticate(authenticationToken);
    }


}
