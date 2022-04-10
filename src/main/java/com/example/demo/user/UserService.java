package com.example.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service  @Transactional @Slf4j
public class UserService {
    private final UserRepo userRepo;
    @Autowired
    public UserService( UserRepo userRepo) {
        this.userRepo = userRepo;
    }

     public AppUser saveUser(AppUser user){
        log.info("saving new user {} to the databas", user.getName());
        return userRepo.save(user);
    };
    public void addRole(String username, String roleName){
        log.info("adding a role {} to user {} ", roleName, username);
        AppUser user = userRepo.findByUsername(username);
        if (user != null ){
            user.getRoles().add(roleName);
        }
    }

   public AppUser getUser(String username){
        log.info("fetching user {}", username);
        return userRepo.findByUsername(username);
    }
    public List<AppUser> getUsers(){
        log.info("fetching all the users");
        return userRepo.findAll();
    }
}
