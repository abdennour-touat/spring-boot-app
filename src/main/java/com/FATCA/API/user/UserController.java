package com.FATCA.API.user;

import com.FATCA.API.security.JWTUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/*
* The user controller contains the methods to make the http requests
* */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Slf4j
public class UserController {
    private final UserService userService;
    private final JWTUtility jwtUtility;
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }
    @GetMapping("/role")
    public ResponseEntity<List<String>> getRoles(){
        return ResponseEntity.ok().body(Roles.getRoles());

    }

    @PostMapping("/role/affectrole")
    public ResponseEntity<?> affectRole(@RequestBody RoleToUser form) {
        userService.addRole(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }
    //this method is to refresh the token when it expires..
    @GetMapping("/token/refresh")
    public void refreshToken (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refresh_token = jwtUtility.getToken(authorizationHeader);
                String username = jwtUtility.getUsername(refresh_token);
                System.out.println("username "+ username);
                UserDetails user = userService.loadUserByUsername(username);

                Map<String, String> tokens = jwtUtility.generateTokens(user, request.getRequestURL().toString());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
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
            throw new RuntimeException("refresh token missing");
        }
    }

}
@Data
class RoleToUser {
    private String username;
    private String roleName;
}
