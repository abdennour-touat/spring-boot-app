package com.FATCA.API.table;

import com.FATCA.API.history.History;
import com.FATCA.API.user.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;

@Entity

@RequiredArgsConstructor
@Getter
@Setter
@ToString
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
//    @JsonIgnore
    private ArrayList<List<HashMap<String, String>>> data ;
    @Column
    private String fileName;

    @Column
    private Date uploadDate;


    @ManyToOne
    @JoinColumn(name = "owner_user_id", foreignKey = @ForeignKey(name = "FK_table_user"))
//    @JsonIgnore()
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser owner;

    public DataTable(AppUser user, ArrayList<List<HashMap<String, String>>> data, String fileName){
        this.data = data;
        this.owner = user;
        this.uploadDate = new Date();
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DataTable dataTable = (DataTable) o;
        return id != null && Objects.equals(id, dataTable.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
