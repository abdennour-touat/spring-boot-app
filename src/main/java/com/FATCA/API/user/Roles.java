package com.FATCA.API.user;

import java.util.List;

//the roles class contains the roles
public class Roles {
    public static String ROLE_ADMIN = "ROLE_ADMIN";
    public static  String ROLE_EDITOR = "ROLE_EDITOR";
    public static String ROLE_USER = "ROLE_USER";
    public static List<String> getRoles (){
        return List.of(ROLE_ADMIN, ROLE_EDITOR, ROLE_EDITOR);
    }
}
