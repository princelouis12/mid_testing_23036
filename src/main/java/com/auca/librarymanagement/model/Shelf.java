package com.auca.librarymanagement.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shelf")
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shelf_id")
    private UUID shelf_id;

    @Column(name = "book_category", nullable = false)
    private String book_category;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "available_stock", nullable = false)
    private Integer available_stock = 0;

    @Column(name = "borrowed_number", nullable = false)
    private Integer borrowed_number = 0;

    @Column(name = "initial_stock", nullable = false)
    private Integer initial_stock = 0;

    @OneToMany(mappedBy = "shelf")
    private List<Book> books;

    public Shelf() {}

    public Shelf(String book_category, Room room, Integer initial_stock) {
        this.book_category = book_category;
        this.room = room;
        this.initial_stock = initial_stock;
        this.available_stock = initial_stock;
        this.borrowed_number = 0;
    }

    // Getters and Setters
    public UUID getShelf_id() { return shelf_id; }

    public String getBook_category() { return book_category; }
    public void setBook_category(String book_category) { this.book_category = book_category; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Integer getAvailable_stock() { return available_stock; }
    public void setAvailable_stock(Integer available_stock) { this.available_stock = available_stock; }

    public Integer getBorrowed_number() { return borrowed_number; }
    public void setBorrowed_number(Integer borrowed_number) { this.borrowed_number = borrowed_number; }

    public Integer getInitial_stock() { return initial_stock; }
    public void setInitial_stock(Integer initial_stock) { this.initial_stock = initial_stock; }

    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}