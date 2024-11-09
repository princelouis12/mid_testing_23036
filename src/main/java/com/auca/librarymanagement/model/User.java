package com.auca.librarymanagement.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "user_name"),
    @Index(name = "idx_role", columnList = "role")
})
public class User extends Person {
    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,50}$", message = "Username must be 4-50 characters and alphanumeric")
    private String userName;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    private Location village;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Borrower> borrowings = new ArrayList<>();

    // Constructors
    public User() {
        super();
    }

    public User(UUID personId, String firstName, String lastName, Gender gender, String phoneNumber,
                String userName, String password, RoleType role, Location village) {
        super(personId, firstName, lastName, gender, phoneNumber);
        this.userName = userName;
        this.setPassword(password);
        this.role = role;
        this.village = village;
    }

    // Password handling methods
    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.password);
    }

    // Business logic methods
    public boolean canBorrowBooks() {
        return (role == RoleType.STUDENT || role == RoleType.TEACHER) && 
               hasActiveMembership() &&
               !hasUnpaidFines();
    }

    public boolean hasActiveMembership() {
        if (memberships == null || memberships.isEmpty()) {
            return false;
        }
        return memberships.stream()
                .anyMatch(Membership::isActive);
    }

    public boolean hasUnpaidFines() {
        if (borrowings == null || borrowings.isEmpty()) {
            return false;
        }
        return borrowings.stream()
                .mapToInt(Borrower::getFine)
                .sum() > 0;
    }

    public boolean isLibrarian() {
        return role == RoleType.LIBRARIAN;
    }

    public boolean isAdministrator() {
        return role == RoleType.HOD || role == RoleType.DEAN || role == RoleType.MANAGER;
    }

    public boolean canManageBooks() {
        return isLibrarian();
    }

    public boolean canApproveMemberships() {
        return isLibrarian();
    }

    public boolean canViewAllDetails() {
        return isLibrarian() || isAdministrator();
    }

    public String getProvinceName() {
        Location current = this.village;
        while (current != null && current.getParent() != null && 
               current.getLocationType() != LocationType.PROVINCE) {
            current = current.getParent();
        }
        return current != null ? current.getLocationName() : null;
    }

    // Validation methods
    @PrePersist
    @PreUpdate
    private void validateUser() {
        validateUsername();
        validateRole();
        validateVillage();
    }

    private void validateUsername() {
        if (userName == null || !userName.matches("^[a-zA-Z0-9_]{4,50}$")) {
            throw new IllegalStateException("Invalid username format");
        }
    }

    private void validateRole() {
        if (isAdministrator() && !memberships.isEmpty()) {
            throw new IllegalStateException("Administrators cannot have memberships");
        }
    }

    private void validateVillage() {
        if (village == null) {
            throw new IllegalStateException("User must be assigned to a village");
        }
        if (village.getLocationType() != LocationType.VILLAGE) {
            throw new IllegalStateException("User must be assigned to a VILLAGE type location");
        }
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Location getVillage() {
        return village;
    }

    public void setVillage(Location village) {
        this.village = village;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }

    public List<Borrower> getBorrowings() {
        return borrowings;
    }

    public void setBorrowings(List<Borrower> borrowings) {
        this.borrowings = borrowings;
    }

    // Helper methods for managing relationships
    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setUser(this);
    }

    public void removeMembership(Membership membership) {
        memberships.remove(membership);
        membership.setUser(null);
    }

    public void addBorrowing(Borrower borrowing) {
        borrowings.add(borrowing);
        borrowing.setReader(this);
    }

    public void removeBorrowing(Borrower borrowing) {
        borrowings.remove(borrowing);
        borrowing.setReader(null);
    }

    @Override
    public String toString() {
        return "User{" +
            "personId='" + getPersonId() + '\'' +
            ", firstName='" + getFirstName() + '\'' +
            ", lastName='" + getLastName() + '\'' +
            ", gender=" + getGender() +
            ", phoneNumber='" + getPhoneNumber() + '\'' +
            ", userName='" + userName + '\'' +
            ", role=" + role +
            ", village=" + (village != null ? village.getLocationName() : "null") +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getPersonId() != null && getPersonId().equals(user.getPersonId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}