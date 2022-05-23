package com.FATCA.API.history;

import com.FATCA.API.table.DataTable;
import com.FATCA.API.user.AppUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class History {
    @Id
    @SequenceGenerator(
            name = "history_sequence",
            sequenceName = "history_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "history_sequence"
    )
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "History_text", nullable = false)
    private ArrayList<String> text;
    private Date date;

//    @OneToOne(mappedBy = "data_table_id")
    @PrimaryKeyJoinColumn
    @OneToOne
    @JsonIgnore
    private DataTable  table;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user_history"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser historyUser ;


    public History(List<String> text, AppUser user){
        this.date = new Date();
        this.text = (ArrayList<String>) text;
        this.historyUser  = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        History history = (History) o;
        return id != null && Objects.equals(id, history.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
