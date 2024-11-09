package com.auca.librarymanagement.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "membership_type")
public class MembershipType {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "membership_type_id", updatable = false, nullable = false)
    private UUID membershipTypeId;

    @Column(name = "max_books", nullable = false)
    private Integer maxBooks;

    @Column(name = "membership_name", nullable = false, unique = true)
    private String membershipName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "membershipType")
    private List<Membership> memberships = new ArrayList<>();

    // Constructors
    public MembershipType() {}

    public MembershipType(String membershipName) {
        this.membershipName = membershipName;
        setDefaultValues();
    }

    public MembershipType(String membershipName, Integer maxBooks, Integer price) {
        this.membershipName = membershipName;
        this.maxBooks = maxBooks;
        this.price = price;
    }

    private void setDefaultValues() {
        switch(membershipName.toUpperCase()) {
            case "GOLD" -> {
                this.price = 50;
                this.maxBooks = 5;
            }
            case "SILVER" -> {
                this.price = 30;
                this.maxBooks = 3;
            }
            case "STRIVER" -> {
                this.price = 10;
                this.maxBooks = 2;
            }
        }
    }
    
 // Add validation to ensure only allowed membership names
    @PrePersist
    @PreUpdate
    private void validateMembershipName() {
        String name = membershipName.toUpperCase();
        if (!name.equals("GOLD") && !name.equals("SILVER") && !name.equals("STRIVER")) {
            throw new IllegalStateException("Invalid membership name. Must be GOLD, SILVER, or STRIVER");
        }
    }

    // Getters and Setters
    public UUID getMembershipTypeId() { return membershipTypeId; }

    public Integer getMaxBooks() { return maxBooks; }
    public void setMaxBooks(Integer maxBooks) { this.maxBooks = maxBooks; }

    public String getMembershipName() { return membershipName; }
    public void setMembershipName(String membershipName) { 
        this.membershipName = membershipName;
        setDefaultValues();
    }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public List<Membership> getMemberships() { return memberships; }
    public void setMemberships(List<Membership> memberships) { this.memberships = memberships; }
}