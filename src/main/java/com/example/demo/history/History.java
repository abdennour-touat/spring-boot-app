package com.example.demo.history;

import com.example.demo.user.AppUser;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@RequiredArgsConstructor
@Data
public class History {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "History_text", nullable = false)
    private String text;
    private Date date = new Date();
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private AppUser user ;


}
