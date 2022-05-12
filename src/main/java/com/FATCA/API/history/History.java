package com.FATCA.API.history;

import com.FATCA.API.user.AppUser;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    @ManyToMany()
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_history"))
    private List<AppUser> historyUsers ;


}
