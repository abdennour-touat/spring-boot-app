package com.example.demo.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Role {
    public static String ROLE_ADMIN = "ROLE_ADMIN";
    public static String ROLE_USER = "ROLE_USER";
    public static String ROLE_EDITOR = "ROLE_EDiTOR";
    public static List<String> roles = List.of(ROLE_ADMIN, ROLE_USER, ROLE_EDITOR);

    public static List<String>  getRoles (){
        return roles;
    }

}
