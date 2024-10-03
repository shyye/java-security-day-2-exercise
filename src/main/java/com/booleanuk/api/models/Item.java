package com.booleanuk.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Simplified version, instead of multiple Java classes I use a type field
    // for book, cd, dvd, videogame, boardgame
    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "creator")
    private String creator;

    @Column(name = "genre")
    private String genre;

    @Column(name = "year")
    private String year;

    @Column(name = "isBorrowed")
    private Boolean isBorrowed = false;

    public Item(int id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans;
}
