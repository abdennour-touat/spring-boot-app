package com.FATCA.API.table;

import com.FATCA.API.user.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@RequiredArgsConstructor
@Data
@javax.persistence.Table(name = "data_table")
public class DataTable {
    @Id
    @SequenceGenerator(
            name = "table_sequence",
            sequenceName = "table_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "table_sequence"
    )
    @Column(
            name = "table_id",
            updatable = false
    )
    private Long id;
    @Column()
    private ArrayList<String[]> data ;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", foreignKey = @ForeignKey(name = "FK_table_user"))
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser owner;

    public DataTable(AppUser user, ArrayList<String[]> data){
        this.data = data;
        this.owner = user;
    }

}
