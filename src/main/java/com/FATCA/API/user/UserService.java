package com.FATCA.API.user;

import com.FATCA.API.table.DataTable;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/*
 * The user service class contains functions to get the user data from the database
 * */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    //this function search for a user in the database and
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(username);
        if (user == null) {
            log.error("user not found");
            throw new UsernameNotFoundException("user not found");
        } else {
            log.info("user found in the database {}", username);
        }
        //we make a list of granted authroties to add it to the user details service
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }


    public AppUser saveUser(AppUser user) {
        log.info("saving new user {} to the database", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    ;

    public void addRole(String username, String roleName, String newUsename) {
        log.info("adding a role {} to user {} ", roleName, username);
        AppUser user = userRepo.findByUsername(username);
        if (user != null) {
            if (!Objects.equals(newUsename, "")){

                user.setUsername(newUsename);
            }
            user.setRole(roleName);
        }
    }
    public void deleteUser (Long userId){

        AppUser user = userRepo.getById(userId);
        System.out.println(user);
        userRepo.delete(user);
    }

    public AppUser getUser(String username) {
        log.info("fetching user {}", username);
        return userRepo.findByUsername(username);
    }

    public List<AppUser> getUsers() {
        log.info("fetching all the users");
        return userRepo.findAll();
    }
}
