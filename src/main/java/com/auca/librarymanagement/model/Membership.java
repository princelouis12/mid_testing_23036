package com.auca.librarymanagement.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "membership")
public class Membership {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "membership_id", updatable = false, nullable = false)
    private UUID membershipId;

    @Column(name = "expiring_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expiringTime;

    @Column(name = "membership_code", nullable = false, unique = true)
    private String membershipCode;

    @ManyToOne(fetch = FetchType.EAGER) // Change to EAGER loading
    @JoinColumn(name = "membership_type_id", nullable = false)
    private MembershipType membershipType;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status", nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.EAGER) // Change to EAGER loading
    @JoinColumn(name = "reader_id", nullable = false)
    private User user;

    @Column(name = "registration_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @OneToMany(mappedBy = "membership")
    private List<Borrower> borrowings = new ArrayList<>();

    // Constructors
    public Membership() {}

    public Membership(String membershipCode, MembershipType membershipType, User user, 
                     Date registrationDate, Date expiringTime) {
        this.membershipCode = membershipCode;
        this.membershipType = membershipType;
        this.user = user;
        this.registrationDate = registrationDate;
        this.expiringTime = expiringTime;
        this.status = Status.PENDING;
    }

    // Business methods
    public boolean canBorrowMore() {
        if (!isActive()) {
            return false;
        }
        
        long currentlyBorrowed = borrowings.stream()
            .filter(b -> b.getReturnDate() == null)
            .count();
        return currentlyBorrowed < membershipType.getMaxBooks();
    }

    public boolean isActive() {
        return status == Status.APPROVED && 
               new Date().before(expiringTime) &&
               !hasUnpaidFines();
    }

    private boolean hasUnpaidFines() {
        return borrowings.stream()
            .mapToInt(Borrower::getFine)
            .sum() > 0;
    }
    
    @PrePersist
    private void setDefaultValues() {
        if (this.registrationDate == null) {
            this.registrationDate = new Date();
        }
        this.status = Status.PENDING;
        this.membershipCode = generateMembershipCode();
    }

    private String generateMembershipCode() {
        return "MC" + String.format("%03d", new Random().nextInt(1000));
    }

    // Getters and Setters
    public UUID getMembershipId() { return membershipId; }

    public Date getExpiringTime() { return expiringTime; }
    public void setExpiringTime(Date expiringTime) { this.expiringTime = expiringTime; }

    public String getMembershipCode() { return membershipCode; }
    public void setMembershipCode(String membershipCode) { this.membershipCode = membershipCode; }

    public MembershipType getMembershipType() { return membershipType; }
    public void setMembershipType(MembershipType membershipType) { this.membershipType = membershipType; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }

    public List<Borrower> getBorrowings() { return borrowings; }
    public void setBorrowings(List<Borrower> borrowings) { this.borrowings = borrowings; }
}