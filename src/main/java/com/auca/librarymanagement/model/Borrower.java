package com.auca.librarymanagement.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "borrower", indexes = {
    @Index(name = "idx_due_date", columnList = "due_date"),
    @Index(name = "idx_return_date", columnList = "return_date"),
    @Index(name = "idx_pickup_date", columnList = "pickup_date")
})
public class Borrower {
    // Late fee constants as per project requirements
    public static final int GOLD_LATE_FEE = 50;    // 50 Rwf per day
    public static final int SILVER_LATE_FEE = 30;  // 30 Rwf per day
    public static final int STRIVER_LATE_FEE = 10; // 10 Rwf per day

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is required")
    private Book book;

    @Column(name = "due_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Due date is required")
    private Date dueDate;

    @Column(nullable = false)
    private Integer fine = 0;

    @Column(name = "late_charge_fees", nullable = false)
    private Integer lateChargeFees = 0;

    @Column(name = "pickup_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Pickup date is required")
    private Date pickupDate;

    @Column(name = "return_date")
    @Temporal(TemporalType.DATE)
    private Date returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", nullable = false)
    @NotNull(message = "Reader is required")
    private User reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    @NotNull(message = "Membership is required")
    private Membership membership;

    // Constructors
    public Borrower() {}

    public Borrower(Book book, User reader, Membership membership, Date pickupDate, Date dueDate) {
        this.book = book;
        this.reader = reader;
        this.membership = membership;
        this.pickupDate = pickupDate;
        this.dueDate = dueDate;
        this.fine = 0;
        this.lateChargeFees = 0;
    }

    // Business logic methods
    public void calculateLateFees() {
        if (returnDate != null && returnDate.after(dueDate)) {
            long diffInMillies = returnDate.getTime() - dueDate.getTime();
            long daysLate = diffInMillies / (24 * 60 * 60 * 1000);
            
            int feePerDay;
            String membershipName = membership.getMembershipType().getMembershipName().toUpperCase();
            
            switch(membershipName) {
                case "GOLD":
                    feePerDay = GOLD_LATE_FEE;
                    break;
                case "SILVER":
                    feePerDay = SILVER_LATE_FEE;
                    break;
                case "STRIVER":
                    feePerDay = STRIVER_LATE_FEE;
                    break;
                default:
                    feePerDay = STRIVER_LATE_FEE;
            }
            
            this.fine = (int) (daysLate * feePerDay);
            this.lateChargeFees = this.fine;
        }
    }

    public boolean isOverdue() {
        if (returnDate == null) {
            return new Date().after(dueDate);
        }
        return returnDate.after(dueDate);
    }

    // Validation methods
    @PrePersist
    @PreUpdate
    private void validateBorrowing() {
        validateDates();
        validateBookAvailability();
        validateMembership();
    }

    private void validateDates() {
        if (pickupDate.after(dueDate)) {
            throw new IllegalStateException("Pickup date cannot be after due date");
        }
        
        if (returnDate != null && returnDate.before(pickupDate)) {
            throw new IllegalStateException("Return date cannot be before pickup date");
        }
    }

    private void validateBookAvailability() {
        if (book != null && pickupDate != null && book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available for borrowing");
        }
    }

    private void validateMembership() {
        if (!membership.isActive()) {
            throw new IllegalStateException("Membership is not active");
        }
        if (!membership.canBorrowMore()) {
            throw new IllegalStateException("Member has reached maximum number of books allowed");
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Integer getFine() { return fine; }
    public void setFine(Integer fine) { this.fine = fine; }

    public Integer getLateChargeFees() { return lateChargeFees; }
    public void setLateChargeFees(Integer lateChargeFees) { this.lateChargeFees = lateChargeFees; }

    public Date getPickupDate() { return pickupDate; }
    public void setPickupDate(Date pickupDate) { this.pickupDate = pickupDate; }

    public User getReader() { return reader; }
    public void setReader(User reader) { this.reader = reader; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null) {
            calculateLateFees();
        }
    }

    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }
}