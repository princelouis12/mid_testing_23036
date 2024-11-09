package com.auca.librarymanagement.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.auca.librarymanagement.model.Location;
import com.auca.librarymanagement.model.LocationType;

import util.HibernateUtil;

public class LocationDao {
	
	
	public void debugLocationHierarchy(UUID locationId) {
        try (Session session = HibernateUtil.openSession()) {
            System.out.println("\n=== Debug Location Hierarchy ===");
            Location location = session.get(Location.class, locationId);
            if (location != null) {
                System.out.println("Location: " + location.getLocationName() + 
                    " (ID: " + location.getLocationId() + ")");
                
                // Print parent chain
                Location parent = location.getParent();
                while (parent != null) {
                    System.out.println("Parent: " + parent.getLocationName() + 
                        " (Type: " + parent.getLocationType() +
                        ", ID: " + parent.getLocationId() + ")");
                    parent = parent.getParent();
                }
            } else {
                System.out.println("No location found with ID: " + locationId);
            }
            System.out.println("=== End Debug ===\n");
        }
    }
    
	public List<Location> getChildLocations(UUID parentId) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            System.out.println("\n=== getChildLocations - Start ===");
            System.out.println("Parent ID (input): " + parentId);
            
            // First get and verify the parent
            Location parent = session.get(Location.class, parentId);
            if (parent == null) {
                System.err.println("Parent not found in database. ID: " + parentId);
                throw new IllegalArgumentException("Parent location not found: " + parentId);
            }

            System.out.println("Found parent in database:");
            System.out.println("- Name: " + parent.getLocationName());
            System.out.println("- Type: " + parent.getLocationType());
            System.out.println("- ID: " + parent.getLocationId());
            System.out.println("- Code: " + parent.getLocationCode());

            // Use criteria API for type-safe query with eager loading
            String hql = """
                SELECT DISTINCT l FROM Location l
                LEFT JOIN FETCH l.parent
                WHERE l.parent.locationId = :parentId
                ORDER BY l.locationName
            """;

            List<Location> children = session.createQuery(hql, Location.class)
                .setParameter("parentId", parentId)
                .list();

            System.out.println("\nFound " + children.size() + " children:");
            children.forEach(child -> {
                System.out.println("\nChild details:");
                System.out.println("- Name: " + child.getLocationName());
                System.out.println("- Type: " + child.getLocationType());
                System.out.println("- ID: " + child.getLocationId());
                System.out.println("- Code: " + child.getLocationCode());
                System.out.println("- Parent ID: " + child.getParent().getLocationId());
            });

            transaction.commit();
            return children;

        } catch (Exception e) {
            System.err.println("Error in getChildLocations: " + e.getMessage());
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
	
	public void verifyLocationHierarchy() {
	    try (Session session = HibernateUtil.openSession()) {
	        System.out.println("\n=== Verifying Location Hierarchy ===");
	        
	        // Get all locations
	        List<Location> allLocations = session.createQuery(
	            "FROM Location l ORDER BY l.locationType, l.locationName", 
	            Location.class
	        ).list();
	        
	        System.out.println("Total locations: " + allLocations.size());
	        
	        // Check each location's parent-child relationship
	        for (Location loc : allLocations) {
	            System.out.println("\nChecking: " + loc.getLocationName() + 
	                " (Type: " + loc.getLocationType() + 
	                ", ID: " + loc.getLocationId() + ")");
	            
	            if (loc.getParent() != null) {
	                Location parent = session.get(Location.class, loc.getParent().getLocationId());
	                if (parent == null) {
	                    System.err.println("ERROR: Parent not found for " + 
	                        loc.getLocationName() + " (ID: " + loc.getLocationId() + ")");
	                } else {
	                    System.out.println("Parent verified: " + parent.getLocationName() + 
	                        " (ID: " + parent.getLocationId() + ")");
	                }
	            }
	        }
	        System.out.println("=== Verification Complete ===\n");
	    }
	}

	public List<Location> getLocationsByType(LocationType locationType) {
	    Session session = null;
	    Transaction transaction = null;
	    try {
	        session = HibernateUtil.openSession();
	        transaction = session.beginTransaction();

	        System.out.println("\n=== Fetching locations of type: " + locationType + " ===");
	        
	        // Updated HQL query to match your database structure
	        String hql = """
	            SELECT l FROM Location l 
	            WHERE l.locationType = :type 
	            ORDER BY l.locationName
	        """;
	        
	        List<Location> locations = session.createQuery(hql, Location.class)
	            .setParameter("type", locationType)
	            .list();
	        
	        // Debug print results
	        System.out.println("Found " + locations.size() + " " + locationType + " locations:");
	        for (Location loc : locations) {
	            System.out.println(String.format(
	                "- Name: %s, Code: %s, ID: %s",
	                loc.getLocationName(),
	                loc.getLocationCode(),
	                loc.getLocationId()
	            ));
	        }
	        
	        transaction.commit();
	        return locations;

	    } catch (Exception e) {
	        System.err.println("Error fetching " + locationType + " locations: " + e.getMessage());
	        e.printStackTrace();
	        if (transaction != null && transaction.isActive()) {
	            transaction.rollback();
	        }
	        return new ArrayList<>();
	    } finally {
	        if (session != null && session.isOpen()) {
	            session.close();
	        }
	    }
	}

    public Location saveLocation(Location location) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            // Set audit fields
            if (location.getCreatedAt() == null) {
                location.setCreatedAt(LocalDateTime.now());
            }
            location.setUpdatedAt(LocalDateTime.now());
            
            // Validate location code format
            validateLocationCode(location);
            
            // Validate hierarchy
            validateHierarchy(location);
            
            session.persist(location);
            transaction.commit();
            return location;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private void validateLocationCode(Location location) {
        String expectedPrefix = switch (location.getLocationType()) {
            case PROVINCE -> "PRV";
            case DISTRICT -> "DST";
            case SECTOR -> "SEC";
            case CELL -> "CEL";
            case VILLAGE -> "VIL";
        };
        
        if (!location.getLocationCode().matches("^" + expectedPrefix + "\\d{3}$")) {
            throw new IllegalArgumentException(
                "Invalid location code format. Expected format: " + expectedPrefix + "XXX"
            );
        }
    }

    private void validateHierarchy(Location location) {
        if (location.getLocationType() == LocationType.PROVINCE && location.getParent() != null) {
            throw new IllegalArgumentException("Province cannot have a parent location");
        }
        
        if (location.getLocationType() != LocationType.PROVINCE && location.getParent() == null) {
            throw new IllegalArgumentException(location.getLocationType() + " must have a parent location");
        }
        
        if (location.getParent() != null) {
            LocationType expectedParentType = switch (location.getLocationType()) {
                case DISTRICT -> LocationType.PROVINCE;
                case SECTOR -> LocationType.DISTRICT;
                case CELL -> LocationType.SECTOR;
                case VILLAGE -> LocationType.CELL;
                case PROVINCE -> throw new IllegalArgumentException("Province cannot have a parent");
            };
            
            if (location.getParent().getLocationType() != expectedParentType) {
                throw new IllegalArgumentException(
                    location.getLocationType() + " must have a " + expectedParentType + " as parent"
                );
            }
        }
    }

    public Location getLocationById(UUID locationId) {
        try (Session session = HibernateUtil.openSession()) {
            System.out.println("\n=== Getting Location By ID ===");
            System.out.println("Searching for ID: " + locationId);
            
            Location location = session.get(Location.class, locationId);
            
            if (location != null) {
                System.out.println("Found location:");
                System.out.println("- Name: " + location.getLocationName());
                System.out.println("- Type: " + location.getLocationType());
                System.out.println("- Code: " + location.getLocationCode());
                System.out.println("- ID: " + location.getLocationId());
                if (location.getParent() != null) {
                    System.out.println("- Parent: " + location.getParent().getLocationName() + 
                                     " (ID: " + location.getParent().getLocationId() + ")");
                }
            } else {
                System.err.println("No location found with ID: " + locationId);
            }
            
            System.out.println("=== Get Location Complete ===\n");
            return location;
        } catch (Exception e) {
            System.err.println("Error getting location by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
 // Helper method to get full location path
    public String getFullLocationPath(Location location) {
        if (location == null) return "";
        
        List<String> path = new ArrayList<>();
        Location current = location;
        
        // Build path from current location up to province
        while (current != null) {
            path.add(0, current.getLocationName());
            current = current.getParent();
        }
        
        return String.join(" > ", path);
    }

    public List<Location> getAllLocations() {
        try (Session session = HibernateUtil.openSession()) {
            @SuppressWarnings("unchecked")
            List<Location> locations = session.createQuery(
                "FROM Location l LEFT JOIN FETCH l.parent ORDER BY l.locationType, l.locationName"
            ).list();
            return locations;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteLocation(UUID locationId) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            Location location = session.get(Location.class, locationId);
            if (location != null) {
                // Check for child locations
                if (!location.getChildren().isEmpty()) {
                    throw new IllegalStateException(
                        "Cannot delete location that has child locations. Remove child locations first."
                    );
                }
                
                // Check for associated users (if it's a village)
                if (location.getLocationType() == LocationType.VILLAGE && !location.getUsers().isEmpty()) {
                    throw new IllegalStateException(
                        "Cannot delete village that has associated users. Reassign users first."
                    );
                }
                
                session.remove(location);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void updateLocation(Location location) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            
            location.setUpdatedAt(LocalDateTime.now());
            validateLocationCode(location);
            validateHierarchy(location);
            
            session.merge(location);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    public static class HibernateContextListener implements javax.servlet.ServletContextListener {
        @Override
        public void contextInitialized(javax.servlet.ServletContextEvent sce) {
            // Initialize Hibernate when the application starts
            try {
                System.out.println("Initializing Hibernate...");
                HibernateUtil.openSession().close();
                System.out.println("Hibernate initialization completed successfully");
            } catch (Exception e) {
                System.err.println("Error initializing Hibernate: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void contextDestroyed(javax.servlet.ServletContextEvent sce) {
            // Clean up Hibernate resources when the application stops
            try {
                System.out.println("Shutting down Hibernate...");
                HibernateUtil.shutdown();
                System.out.println("Hibernate shutdown completed successfully");
            } catch (Exception e) {
                System.err.println("Error shutting down Hibernate: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    
    
    
}