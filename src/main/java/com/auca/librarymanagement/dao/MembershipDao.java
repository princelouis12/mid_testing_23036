package com.auca.librarymanagement.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import com.auca.librarymanagement.model.*;
import util.HibernateUtil;

public class MembershipDao {
    
    public Membership createMembership(Membership membership) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Refresh references to ensure they are attached to current session
            if (membership.getMembershipType() != null) {
                membership.setMembershipType(
                    session.get(MembershipType.class, membership.getMembershipType().getMembershipTypeId())
                );
            }
            if (membership.getUser() != null) {
                membership.setUser(
                    session.get(User.class, membership.getUser().getPersonId())
                );
            }
            
            session.save(membership);
            transaction.commit();
            return membership;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating membership: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
 // Method to directly verify database connectivity and table content
    public void verifyDatabaseContent() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check Membership table
            String sql = "SELECT COUNT(*) FROM membership";
            Long membershipCount = ((Number) session.createNativeQuery(sql).getSingleResult()).longValue();
            System.out.println("Direct SQL count of memberships: " + membershipCount);
            
            // Get sample data if any exists
            if (membershipCount > 0) {
                String sampleSql = "SELECT membership_id, membership_code, membership_status FROM membership LIMIT 2";
                List<Object[]> samples = session.createNativeQuery(sampleSql).getResultList();
                System.out.println("\nSample membership data:");
                for (Object[] sample : samples) {
                    System.out.println("ID: " + sample[0] + ", Code: " + sample[1] + ", Status: " + sample[2]);
                }
            }
        } catch (Exception e) {
            System.err.println("Database verification error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Membership getActiveMembershipForUser(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.user.personId = :userId " +
                        "AND m.status = :status " +
                        "AND m.expiringTime > :currentDate " +
                        "ORDER BY m.registrationDate DESC";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("userId", userId);
            query.setParameter("status", Status.APPROVED);
            query.setParameter("currentDate", new Date());
            query.setMaxResults(1);
            
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active membership: " + e.getMessage(), e);
        }
    }
    
    public int getCurrentBorrowingsCount(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(b) FROM Borrower b " +
                        "WHERE b.reader.personId = :userId " +
                        "AND b.returnDate IS NULL";
            
            TypedQuery<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("userId", userId);
            return query.getSingleResult().intValue();
        } catch (Exception e) {
            throw new RuntimeException("Error counting current borrowings: " + e.getMessage(), e);
        }
    }
    
    public int getUnpaidFines(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COALESCE(SUM(b.fine), 0) FROM Borrower b " +
                        "WHERE b.reader.personId = :userId";
            
            TypedQuery<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("userId", userId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating unpaid fines: " + e.getMessage(), e);
        }
    }
    
    public Optional<Membership> getActiveMembership(UUID userId) {
        return Optional.ofNullable(getActiveMembershipForUser(userId));
    }


    public void updateMembership(Membership membership) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Refresh associations
            if (membership.getMembershipType() != null) {
                membership.setMembershipType(
                    session.get(MembershipType.class, membership.getMembershipType().getMembershipTypeId())
                );
            }
            if (membership.getUser() != null) {
                membership.setUser(
                    session.get(User.class, membership.getUser().getPersonId())
                );
            }
            
            // Merge and flush to ensure immediate update
            session.merge(membership);
            session.flush();
            
            transaction.commit();
            
            System.out.println("Successfully updated membership status to: " + membership.getStatus());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating membership: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating membership: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Membership getMembershipById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.membershipId = :id";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("id", id);
            
            Membership membership = query.getSingleResult();
            if (membership != null) {
                System.out.println("Found membership: " + membership.getMembershipCode());
            }
            return membership;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding membership: " + e.getMessage(), e);
        }
    }
    
    public boolean existsByMembershipCode(String membershipCode) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT COUNT(m) FROM Membership m WHERE m.membershipCode = :code";
            TypedQuery<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("code", membershipCode);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking membership code existence: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    
    
    public boolean checkMembershipExists(UUID membershipId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(m) FROM Membership m WHERE m.membershipId = :id";
            TypedQuery<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("id", membershipId);
            Long count = query.getSingleResult();
            System.out.println("Checking membership " + membershipId + ": exists = " + (count > 0));
            return count > 0;
        }
    }

    public List<Membership> getMembershipsByUser(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.user.personId = :userId " +
                        "ORDER BY m.registrationDate DESC";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user memberships: " + e.getMessage(), e);
        }
    }

    public List<Membership> getAllMemberships() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // First verify database connectivity
            System.out.println("Testing database connectivity...");
            try {
                // Direct SQL query to check table existence and data
                NativeQuery<?> sqlQuery = session.createNativeQuery("SELECT COUNT(*) FROM membership");
                Number count = (Number) sqlQuery.uniqueResult();
                System.out.println("Direct SQL count: " + count);
                
                // Try to get a sample row
                NativeQuery<?> sampleQuery = session.createNativeQuery(
                    "SELECT membership_id, membership_code, membership_status, membership_type_id, reader_id " +
                    "FROM membership LIMIT 1"
                );
                List<?> sample = sampleQuery.getResultList();
                if (!sample.isEmpty()) {
                    Object[] row = (Object[]) sample.get(0);
                    System.out.println("Sample row found:");
                    System.out.println("- ID: " + row[0]);
                    System.out.println("- Code: " + row[1]);
                    System.out.println("- Status: " + row[2]);
                }
            } catch (Exception e) {
                System.err.println("Error in direct SQL query: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Try HQL query
            String hql = "SELECT DISTINCT m FROM Membership m " +
                        "LEFT JOIN FETCH m.user u " +
                        "LEFT JOIN FETCH m.membershipType mt";
            
            System.out.println("Executing HQL query: " + hql);
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            List<Membership> memberships = query.getResultList();
            
            System.out.println("Query executed. Result size: " + 
                (memberships != null ? memberships.size() : "null"));
            
            if (memberships != null && !memberships.isEmpty()) {
                System.out.println("\nFirst membership details:");
                Membership first = memberships.get(0);
                System.out.println("ID: " + first.getMembershipId());
                System.out.println("Code: " + first.getMembershipCode());
                System.out.println("Status: " + first.getStatus());
                if (first.getUser() != null) {
                    System.out.println("User: " + first.getUser().getFirstName() + 
                        " " + first.getUser().getLastName());
                }
                if (first.getMembershipType() != null) {
                    System.out.println("Type: " + first.getMembershipType().getMembershipName());
                }
            } else {
                System.out.println("No memberships found in result");
            }
            
            return memberships;
            
        } catch (Exception e) {
            System.err.println("Error in getAllMemberships: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching memberships: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    
    public List<Membership> getPendingMemberships() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.status = :status " +
                        "ORDER BY m.registrationDate DESC";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("status", Status.PENDING);
            
            List<Membership> memberships = query.getResultList();
            return memberships;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching pending memberships: " + e.getMessage(), e);
        }
    }

    public boolean updateMembershipStatus(UUID membershipId, Status newStatus) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Get the membership with its associations
            String hql = "SELECT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.membershipId = :id";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("id", membershipId);
            
            Membership membership = query.getSingleResult();
            
            if (membership != null) {
                System.out.println("Updating membership status:");
                System.out.println("- ID: " + membership.getMembershipId());
                System.out.println("- Current Status: " + membership.getStatus());
                System.out.println("- New Status: " + newStatus);
                System.out.println("- Membership Type: " + 
                    (membership.getMembershipType() != null ? 
                    membership.getMembershipType().getMembershipName() : "null"));
                
                membership.setStatus(newStatus);
                session.merge(membership);
                transaction.commit();
                
                System.out.println("Status updated successfully");
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating membership status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating membership status: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Membership> getMembershipsByStatus(Status status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT m FROM Membership m " +
                        "LEFT JOIN FETCH m.membershipType " +
                        "LEFT JOIN FETCH m.user " +
                        "WHERE m.status = :status " +
                        "ORDER BY m.registrationDate DESC";
            
            TypedQuery<Membership> query = session.createQuery(hql, Membership.class);
            query.setParameter("status", status);
            
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching memberships by status: " + e.getMessage(), e);
        }
    }

    public boolean hasUnpaidFines(UUID userId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            Long fineCount = session.createQuery(
                "SELECT COUNT(b) FROM Borrower b " +
                "WHERE b.reader.personId = :userId " +
                "AND b.fine > 0", 
                Long.class
            )
            .setParameter("userId", userId)
            .uniqueResult();
            
            return fineCount > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error checking unpaid fines: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}