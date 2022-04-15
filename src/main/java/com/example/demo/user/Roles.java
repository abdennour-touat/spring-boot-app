package com.example.demo.user;

import java.util.List;

public class Roles {
    public static String ROLE_ADMIN = "ROLE_ADMIN";
    public static  String ROLE_EDITOR = "ROLE_EDITOR";
    public static String ROLE_USER = "ROLE_USER";
    public static List<String> getRoles (){
        return List.of(ROLE_ADMIN, ROLE_EDITOR, ROLE_EDITOR);
    }
}
