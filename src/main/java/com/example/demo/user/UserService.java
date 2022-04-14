package com.example.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Service  @Transactional @Slf4j @RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(username);
        if (user == null){
            log.error("user not found");
            throw new UsernameNotFoundException("user not found");
        }else {
            log.info("user found in the database {}", username);
        }
         Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role ->{
            authorities.add(new SimpleGrantedAuthority(role));
        });
        return new User(user.getUsername(), user.getPassword(),  authorities);
    }


     public AppUser saveUser(AppUser user){
        log.info("saving new user {} to the databas", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
