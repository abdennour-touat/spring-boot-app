package com.FATCA.API.user;

import com.FATCA.API.fileStorage.FilesStorageService;
import com.FATCA.API.security.JWTUtility;
import com.FATCA.API.table.DataTable;
import com.FATCA.API.table.DataTableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
    //DONE:upload csv file
    //Done:get all the csv files
    //DONE:convert a csv file into an xml file (not zipped)
    //DONE: convert and zip the xml file with a password
    //TODO: get an existing csv file
    //TODO: save a csv file
    private final UserService userService;
    private final JWTUtility jwtUtility;
    private final DataTableService dataTableService;
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }
    @GetMapping("/admin/role")
    public ResponseEntity<List<String>> getRoles(){
        return ResponseEntity.ok().body(Roles.getRoles());

    }

    @PostMapping("/admin/role/affectrole")
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

    //upload csv file
    @PostMapping("/uploadcsv")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file, String username) throws Exception {
        ArrayList<String[]> data = dataTableService.csvToArray(file);
        AppUser user = userService.getUser(username);
        dataTableService.addTable(new DataTable(user, data));
        return ResponseEntity.ok().body(data);
    }
    @GetMapping("/getcsvFiles")
    public ResponseEntity<?> getCsvFiles(){
        return ResponseEntity.ok().body(dataTableService.getAllTables());
    }


}
@Data
class RoleToUser {
    private String username;
    private String roleName;
}
