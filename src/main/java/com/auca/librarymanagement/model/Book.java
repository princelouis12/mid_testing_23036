package com.auca.librarymanagement.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "book", indexes = {
    @Index(name = "idx_isbn", columnList = "ISBNCode"),
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_status", columnList = "Book_status")
})
public class Book {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "book_id", updatable = false, nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    @NotNull(message = "Title cannot be null")
    private String title;

    @Column(nullable = false)
    @Min(value = 1, message = "Edition must be at least 1")
    private Integer edition;

    @Column(name = "ISBNCode", nullable = false, unique = true)
    private String isbnCode;

    @Column(name = "publisher_name")
    private String publisherName;

    @Column(name = "publication_year")
    @Temporal(TemporalType.DATE)
    private Date publicationYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_id", nullable = false)
    private Shelf shelf;

    @Enumerated(EnumType.STRING)
    @Column(name = "Book_status", nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;

    @OneToMany(mappedBy = "book")
    private List<Borrower> borrowHistory = new ArrayList<>();

    // Constructors
    public Book() {}

    public Book(String title, Integer edition, String isbnCode, Date publicationYear,
                String publisherName, Shelf shelf) {
        this.title = title;
        this.edition = edition;
        this.isbnCode = isbnCode;
        this.publicationYear = publicationYear;
        this.publisherName = publisherName;
        this.shelf = shelf;
        this.status = BookStatus.AVAILABLE;
    }

    // Getters and Setters
    public UUID getBookId() { return bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getEdition() { return edition; }
    public void setEdition(Integer edition) { this.edition = edition; }

    public String getIsbnCode() { return isbnCode; }
    public void setIsbnCode(String isbnCode) { this.isbnCode = isbnCode; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public Date getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Date publicationYear) { this.publicationYear = publicationYear; }

    public Shelf getShelf() { return shelf; }
    public void setShelf(Shelf shelf) { this.shelf = shelf; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public List<Borrower> getBorrowHistory() { return borrowHistory; }
    public void setBorrowHistory(List<Borrower> borrowHistory) { this.borrowHistory = borrowHistory; }
}