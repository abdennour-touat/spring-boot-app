package com.FATCA.API.user;

import com.FATCA.API.history.History;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
* the appuser is the user class and it'll create a table for the user in the database
* */
@Entity(name = "users")
@Table(name = "users")
@Data
public class AppUser {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(
            name = "user_id",
            updatable = false
    )
    private Long id;
    private  String name;
    @Column(
            nullable = false,
            unique = true
    )
    private String username;
    @Column(
            nullable = false,
            unique = true
    )
    private String password;
    private ArrayList<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<History> userHistory = new HashSet<>();

    public AppUser() {
    }

    public AppUser( String name, String username, String password) {
        this.name = name;
        this.password = password;
        this.username = username;
    }

}
