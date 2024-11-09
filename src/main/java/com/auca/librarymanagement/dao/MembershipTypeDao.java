package com.auca.librarymanagement.dao;

import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.auca.librarymanagement.model.MembershipType;
import util.HibernateUtil;

public class MembershipTypeDao {
    
	public MembershipType getOrCreateMembershipType(String name) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Standardize the name
            String formattedName = name.trim().toUpperCase();
            
            // Try to find existing type
            String hql = "FROM MembershipType mt WHERE mt.membershipName = :name";
            MembershipType type = session.createQuery(hql, MembershipType.class)
                                      .setParameter("name", formattedName)
                                      .uniqueResult();
            
            if (type == null) {
                // Create new if not exists using the constructor that sets default values
                type = new MembershipType(formattedName);
                session.persist(type);
            }
            
            transaction.commit();
            return type;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error getting/creating membership type: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
	public List<MembershipType> getAllMembershipTypes() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery("FROM MembershipType", MembershipType.class)
                         .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching membership types: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
	public MembershipType getMembershipTypeById(UUID id) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.get(MembershipType.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching membership type: " + e.getMessage(), e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}