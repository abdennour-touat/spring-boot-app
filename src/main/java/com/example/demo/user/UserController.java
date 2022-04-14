package com.example.demo.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {
    private final UserService userService;
    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }
//    @GetMapping("/")
//    public String index() {
//        return "Welcome to the home page!";
//    }
    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }
    @GetMapping("/role")
    public ResponseEntity<?> getRoles(){
        return ResponseEntity.ok().body(Roles.values());
    }

    @PostMapping("/role/affectrole")
    public ResponseEntity<?> affectRole(@RequestBody RoleToUser form) {
        userService.addRole(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

}
@Data
class RoleToUser {
    private String username;
    private String roleName;
}
