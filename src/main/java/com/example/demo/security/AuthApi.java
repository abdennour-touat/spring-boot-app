package com.example.demo.security;

import com.example.demo.user.AppUser;
import com.example.demo.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AuthApi  {
//    @Autowired
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtility;
    private final UserService userService;
//    @PostMapping("login")
//    public ResponseEntity<AppUser>login( AppUser appUser){
//        try{
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword())
//            );
//            UserDetails user = (UserDetails) authentication.getPrincipal();
//            return ResponseEntity.ok()
//                    .header(
//                            HttpHeaders.AUTHORIZATION,
//                            jwtTokenUtil.generateToken(user)
//                    ).body(userService.saveUser(appUser));
//        }catch (BadCredentialsException ex){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @PostMapping("/authenticate")
    @ResponseBody
    public ResponseEntity<AppUser> authenticate( AppUser jwtRequest, HttpServletResponse response) throws Exception {

        AppUser jwtResponse = new AppUser();
        Authentication authentication = null;
        System.out.println("Debut authentication.");
        System.out.println(userService.loadUserByUsername(jwtRequest.getUsername()).getUsername());
        /* check if the user exits in our DB */
        if (userService.loadUserByUsername(jwtRequest.getUsername()).getUsername() != "") {
            System.err.println("user exists ? ");

            try {
                /* authenticate the user with the setted credentials */
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        jwtRequest.getUsername(), jwtRequest.getPassword()));
                System.out.println("authentication:"+ authentication);

            } catch (AuthenticationException e) {

                throw new Exception("Bad Credetials !");
            }

            /* get the details of the authentivated user from myUserDetailsService */
            final UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());
            System.out.println("principal : "+authentication);
            System.err.println(userDetails.getAuthorities());
            /* generate token for the authenticated user */
            final String token = jwtUtility.generateToken(userDetails);
//            jwtResponse.set(token);
                response.setHeader("access_token", token);
            /* generate the httpOnly cookie to store the token */
            Cookie jwtCookie = new Cookie("token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setSecure(false);
            //jwtCookie.setDomain("local.portal.dz");
            response.addCookie(jwtCookie);

            /* get the request date time  */
            LocalDateTime requestTimeDate = LocalDateTime.now();

            System.out.println(requestTimeDate);
            Cookie requesTimeCookie = new Cookie("requesTimeCookie", requestTimeDate.toString());
            requesTimeCookie.setHttpOnly(true);
            requesTimeCookie.setSecure(false);
            //requesTimeCookie.setDomain("local.portal.dz");
            requesTimeCookie.setPath("/");
            response.addCookie(requesTimeCookie);

        }else {
            throw new Exception("user does not exists !");
        }

        System.out.println("TOKEN IS SETTED IN THE REST API !!!!!!!");
        //response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Allow-Origin", "http://local.portal.dz:3000");
//		response.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
//		response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Credentials, Access-Control-Allow-Origin");
//
        return new ResponseEntity<AppUser>(jwtResponse, HttpStatus.OK);
    }

}
