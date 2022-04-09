package com.example.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service  @Transactional @Slf4j
public class UserService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    @Autowired
    public UserService(RoleRepo roleRepo, UserRepo userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    AppUSer saveUser(AppUSer user){
        log.info("saving new user {} to the databas", user.getName());
        return userRepo.save(user);
    };
    Role saveRole(Role role){
        log.info("saving new roel{} to the databas", role.getName());
        return roleRepo.save(role);
    };
    void addRole(String username, String roleName){
        log.info("adding a role {} to user {} ", roleName, username);
        AppUSer user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        if (user != null && role != null){
            user.getRoles().add(role);
        }
    }

    AppUSer getUser(String username){
        log.info("fetching user {}", username);
        return userRepo.findByUsername(username);
    }
    List<AppUSer> getUsers(){
        log.info("fetching all the users");
        return userRepo.findAll();
    }
}
