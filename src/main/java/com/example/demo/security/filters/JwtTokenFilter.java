package com.example.demo.security.filters;

import com.example.demo.security.JwtTokenUtil;
import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtUtility;
    private final UserService userService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String userName = null;
        String dateTimeToken = null;
//         final UserDetails userDetails;
        System.out.println(request.getMethod());

        Cookie[] tokencookies = request.getCookies();

        if (tokencookies != null) {
            for (Cookie cookie : tokencookies) {
                if (cookie.getName().equals("requesTimeCookie")) {
                    dateTimeToken = cookie.getValue().replace("T", " ").substring(0,19);
                    System.out.println("dateTimeToken: "+dateTimeToken);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime oldRequesTime = LocalDateTime.parse(dateTimeToken,formatter);
                    System.out.println("oldRequesTime : "+oldRequesTime);
                    /* test if the duration between the two requests is less than session expiration */
                    if (Duration.between(oldRequesTime, LocalDateTime.now()).toSeconds() <  10* 60 * 1000 ) {
                        /* if less than session expiration than replace
                         * the time request cookie with the current time */

                        /* delete the old requesTimeCookie cookie */
                        Cookie cookieToRemove = new Cookie("requesTimeCookie", null);
                        cookieToRemove.setPath("/");
                        cookieToRemove.setMaxAge(0);
                        cookieToRemove.setHttpOnly(true);
                        response.addCookie(cookieToRemove);

                        /* set the new value of the requesTimeCookie */
                        Cookie newTimeCookie = new Cookie("requesTimeCookie", LocalDateTime.now().toString());
                        newTimeCookie.setPath("/");
                        newTimeCookie.setHttpOnly(true);
                        response.addCookie(newTimeCookie);
                    }else {
                        /* if it is not less than the session expiration
                         * than disconnect the user */

                        /* delete the requesTimeCookie cookie */
                        Cookie cookieToRemove = new Cookie("requesTimeCookie", null);
                        cookieToRemove.setPath("/");
                        cookieToRemove.setMaxAge(0);
                        cookieToRemove.setHttpOnly(true);
                        response.addCookie(cookieToRemove);

                        /* delete the token cookie */
                        Cookie tokenToRemove = new Cookie("token", null);
                        tokenToRemove.setPath("/");
                        tokenToRemove.setMaxAge(0);
                        tokenToRemove.setHttpOnly(true);
                        response.addCookie(tokenToRemove);


                    }
                }
            }
        }

        if (tokencookies != null) {
            System.out.println("the coockies: "+tokencookies);
            for (Cookie cookie : tokencookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                    System.out.println("authorization: "+token);
                    if (jwtUtility.isTokenExpired(token)) {
                        /* if the token is expired than delete it from the cookie
                         * to force the use to relogin again */

                        /* delete the requesTimeCookie cookie */
                        Cookie cookieToRemove = new Cookie("requesTimeCookie", null);
                        cookieToRemove.setPath("/");
                        cookieToRemove.setMaxAge(0);
                        cookieToRemove.setHttpOnly(true);
                        response.addCookie(cookieToRemove);

                        /* delete the token cookie */
                        Cookie tokenToRemove = new Cookie("token", null);
                        tokenToRemove.setPath("/");
                        tokenToRemove.setMaxAge(0);
                        tokenToRemove.setHttpOnly(true);
                        response.addCookie(tokenToRemove);
                    }
                }
            }
        }


        System.out.println("**************** "+token);

        /* check if the token is in the header of the request */
        if (null != token ) {

            userName = jwtUtility.getUsernameFromToken(token);
            System.out.println("**************** "+userName);
        }


        if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(userName);

            if (jwtUtility.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }

        }

        System.out.println("TOKEN IS SETTED IN THE FILTER API !!!!!!!");
        //response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Allow-Origin", "http://local.portal.dz:3000");
//		response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
//		response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Access-Control-Allow-Origin");
//
        filterChain.doFilter(request, response);
}
    }
