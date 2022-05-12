package com.FATCA.API.table;

import com.FATCA.API.user.AppUser;
import lombok.*;
import org.hibernate.Hibernate;

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
    private ArrayList<String> data ;


    @ManyToOne
    @JoinColumn(name = "owner_user_id", foreignKey = @ForeignKey(name = "FK_table_user"))
    private AppUser owner;

}
