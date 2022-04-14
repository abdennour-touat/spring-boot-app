package com.example.demo.history;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@RequiredArgsConstructor
@Data
public class History {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;


}
