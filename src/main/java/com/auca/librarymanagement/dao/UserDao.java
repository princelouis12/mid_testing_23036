package com.auca.librarymanagement.dao;

import com.auca.librarymanagement.model.User;
import com.auca.librarymanagement.model.Location;
import com.auca.librarymanagement.model.LocationType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public class UserDao {

    public User createUser(User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();

            // Validate user before persisting
            validateUserBeforeCreate(user, session);
            
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public Optional<User> findByUsername(String userName) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query<User> query = session.createQuery(
                "FROM User u WHERE u.userName = :userName", 
                User.class
            );
            query.setParameter("userName", userName);
            
            try {
                User user = query.uniqueResult();
                return Optional.ofNullable(user);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by username: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public Optional<Location> findVillageById(UUID villageId) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query<Location> query = session.createQuery(
                "FROM Location l WHERE l.locationId = :villageId AND l.locationType = :locationType",
                Location.class
            );
            query.setParameter("villageId", villageId);
            query.setParameter("locationType", LocationType.VILLAGE);
            
            try {
                Location village = query.uniqueResult();
                return Optional.ofNullable(village);
            } catch (NoResultException e) {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding village: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public boolean existsByUsername(String userName) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query<Long> query = session.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.userName = :userName",
                Long.class
            );
            query.setParameter("userName", userName);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking username existence: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public List<User> findAll() {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all users: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public Optional<User> findById(UUID personId) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            User user = session.get(User.class, personId);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by ID: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void update(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public void delete(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.openSession();
            transaction = session.beginTransaction();
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    private void validateUserBeforeCreate(User user, Session session) {
        // Check if username already exists
        if (existsByUsername(user.getUserName())) {
            throw new IllegalStateException("Username already exists: " + user.getUserName());
        }

        // Validate village
        if (user.getVillage() == null) {
            throw new IllegalStateException("User must be assigned to a village");
        }

        // Refresh village to ensure it exists and is of correct type
        session.refresh(user.getVillage());
        if (user.getVillage().getLocationType() != LocationType.VILLAGE) {
            throw new IllegalStateException("Location must be of type VILLAGE");
        }
    }
    
    public List<Location> findAllVillages() {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query<Location> query = session.createQuery(
                "FROM Location l WHERE l.locationType = :type ORDER BY l.locationName", 
                Location.class
            );
            query.setParameter("type", LocationType.VILLAGE);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving villages: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public List<Location> findVillagesByParent(UUID parentId) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            Query<Location> query = session.createQuery(
                "FROM Location l WHERE l.locationType = :type AND l.parent.locationId = :parentId " +
                "ORDER BY l.locationName", 
                Location.class
            );
            query.setParameter("type", LocationType.VILLAGE);
            query.setParameter("parentId", parentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving villages by parent: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public List<Location> findVillagesByProvince(UUID provinceId) {
        Session session = null;
        try {
            session = HibernateUtil.openSession();
            // This query gets villages that are descendants of the specified province
            String hql = """
                FROM Location l 
                WHERE l.locationType = :type 
                AND EXISTS (
                    SELECT 1 
                    FROM Location parent 
                    WHERE parent.locationId = :provinceId 
                    AND (
                        l.parent.locationId = parent.locationId 
                        OR l.parent.parent.locationId = parent.locationId 
                        OR l.parent.parent.parent.locationId = parent.locationId
                    )
                )
                ORDER BY l.locationName
                """;
            
            Query<Location> query = session.createQuery(hql, Location.class);
            query.setParameter("type", LocationType.VILLAGE);
            query.setParameter("provinceId", provinceId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving villages by province: " + e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    // Helper method to get location hierarchy string
    public String getLocationHierarchy(Location village) {
        if (village == null) return "";
        
        StringBuilder hierarchy = new StringBuilder(village.getLocationName());
        Location current = village.getParent();
        
        while (current != null) {
            hierarchy.insert(0, current.getLocationName() + " > ");
            current = current.getParent();
        }
        
        return hierarchy.toString();
    }

}