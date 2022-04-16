package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

//this interface add a new function to find a user by its username..
public interface UserRepo  extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
