package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo  extends JpaRepository<AppUSer, Long> {
    AppUSer findByUsername(String username);
}
