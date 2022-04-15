package com.example.demo.user;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table
@Data
public class AppUser {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    private Long id;
    private  String name;
    private String username;
    private String password;
    private ArrayList<String> roles = new ArrayList<>();

    public AppUser() {
    }

    public AppUser( String name, String username, String password) {
        this.name = name;
        this.password = password;
        this.username = username;
    }

}
