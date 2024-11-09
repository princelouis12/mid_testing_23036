package com.auca.librarymanagement.model;

import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
@Table(indexes = {
    @Index(name = "idx_phone_number", columnList = "phone_number")
})
public abstract class Person extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "person_id", updatable = false, nullable = false)
    private UUID personId;

    @Column(name = "first_name", nullable = false, length = 50)
    @Pattern(regexp = "^[A-Za-z\\s-]{2,50}$", message = "First name must be 2-50 characters long and contain only letters")
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @Pattern(regexp = "^[A-Za-z\\s-]{2,50}$", message = "Last name must be 2-50 characters long and contain only letters")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "phone_number", nullable = false, length = 20)
    @Pattern(regexp = "^07[0-9]{8}$", message = "Phone number must be in format 07XXXXXXXX")
    private String phoneNumber;

    // Constructors, getters, and setters with proper validation
    public Person() {}

    public Person(UUID personId, String firstName, String lastName, Gender gender, String phoneNumber) {
        this.personId = personId;
        setFirstName(firstName);
        setLastName(lastName);
        this.gender = gender;
        setPhoneNumber(phoneNumber);
    }

    // Improved setters with validation
    public void setFirstName(String firstName) {
        if (!firstName.matches("^[A-Za-z\\s-]{2,50}$")) {
            throw new IllegalArgumentException("Invalid first name format");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (!lastName.matches("^[A-Za-z\\s-]{2,50}$")) {
            throw new IllegalArgumentException("Invalid last name format");
        }
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("^07[0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public UUID getPersonId() { return personId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
}