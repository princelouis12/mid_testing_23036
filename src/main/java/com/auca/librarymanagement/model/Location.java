package com.auca.librarymanagement.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "location", indexes = {
    @Index(name = "idx_location_code", columnList = "location_code"),
    @Index(name = "idx_location_type", columnList = "location_type"),
    @Index(name = "idx_parent", columnList = "parent_id")
})
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "location_id", updatable = false, nullable = false)
    private UUID locationId;

    @Column(name = "location_code", nullable = false)
    @Pattern(regexp = "^(PRV|DST|SEC|CEL|VIL)[0-9]{3}$", message = "Invalid location code format")
    private String locationCode;

    @Column(name = "location_name", nullable = false)
    @Pattern(regexp = "^[A-Za-z\\s-]{2,100}$", message = "Location name must be 2-100 characters")
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("locationType, locationName")
    private List<Location> children = new ArrayList<>();

    @OneToMany(mappedBy = "village", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    // Constructors
    public Location() {}

    public Location(String locationCode, String locationName, LocationType locationType, Location parent) {
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.locationType = locationType;
        this.parent = parent;
    }

    // Getters and Setters
    public UUID getLocationId() {
        return locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    public List<Location> getChildren() {
        return children;
    }

    public void setChildren(List<Location> children) {
        this.children = children;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    // Validation methods
    @PrePersist
    @PreUpdate
    private void validateHierarchy() {
        if (locationType == LocationType.PROVINCE && parent != null) {
            throw new IllegalStateException("Province cannot have a parent");
        }
        if (locationType != LocationType.PROVINCE && parent == null) {
            throw new IllegalStateException("Non-province locations must have a parent");
        }
        if (parent != null) {
            validateParentType();
        }
        validateLocationCode();
    }

    private void validateParentType() {
        LocationType expectedParentType = switch (locationType) {
            case VILLAGE -> LocationType.CELL;
            case CELL -> LocationType.SECTOR;
            case SECTOR -> LocationType.DISTRICT;
            case DISTRICT -> LocationType.PROVINCE;
            case PROVINCE -> throw new IllegalStateException("Province cannot have a parent");
        };
        if (parent.getLocationType() != expectedParentType) {
            throw new IllegalStateException(
                String.format("%s must have a %s as parent", locationType, expectedParentType));
        }
    }

    private void validateLocationCode() {
        String expectedPrefix = switch (locationType) {
            case PROVINCE -> "PRV";
            case DISTRICT -> "DST";
            case SECTOR -> "SEC";
            case CELL -> "CEL";
            case VILLAGE -> "VIL";
        };
        if (!locationCode.startsWith(expectedPrefix) || !locationCode.matches("^[A-Z]{3}\\d{3}$")) {
            throw new IllegalStateException("Invalid location code format for " + locationType);
        }
    }

    // Add helper method to add child location
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }

    // Add helper method to remove child location
    public void removeChild(Location child) {
        children.remove(child);
        child.setParent(null);
    }

    @Override
    public String toString() {
        return "Location{" +
            "locationId=" + locationId +
            ", locationCode='" + locationCode + '\'' +
            ", locationName='" + locationName + '\'' +
            ", locationType=" + locationType +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return locationId != null && locationId.equals(location.getLocationId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}