package com.auca.librarymanagement.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private UUID room_id;

    @Column(name = "room_code", nullable = false, unique = true)
    private String room_code;

    @OneToMany(mappedBy = "room")
    private List<Shelf> shelves;

    public Room() {}

    public Room(String room_code) {
        this.room_code = room_code;
    }

    public int getTotalBooks() {
        return shelves.stream()
                     .mapToInt(shelf -> shelf.getAvailable_stock() + shelf.getBorrowed_number())
                     .sum();
    }

    public int getAvailableBooks() {
        return shelves.stream()
                     .mapToInt(Shelf::getAvailable_stock)
                     .sum();
    }

    public int getBorrowedBooks() {
        return shelves.stream()
                     .mapToInt(Shelf::getBorrowed_number)
                     .sum();
    }

    public int getBooksByCategory(String category) {
        return shelves.stream()
                     .filter(shelf -> shelf.getBook_category().equals(category))
                     .mapToInt(shelf -> shelf.getAvailable_stock() + shelf.getBorrowed_number())
                     .sum();
    }

    // Getters and Setters
    public UUID getRoom_id() { 
        return room_id; 
    }

    public String getRoom_code() { 
        return room_code; 
    }
    
    public void setRoom_code(String room_code) { 
        this.room_code = room_code; 
    }

    public List<Shelf> getShelves() { 
        return shelves; 
    }
    
    public void setShelves(List<Shelf> shelves) { 
        this.shelves = shelves; 
    }
}