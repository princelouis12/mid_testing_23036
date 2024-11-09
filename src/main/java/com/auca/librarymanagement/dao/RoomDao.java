// RoomDao.java
package com.auca.librarymanagement.dao;

import com.auca.librarymanagement.model.Room;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.UUID;

public class RoomDao {
    
    public boolean saveRoom(Room room) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Check if room code already exists
            Room existingRoom = getRoomByCode(room.getRoom_code());
            if (existingRoom != null) {
                return false;
            }
            
            session.save(room);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Room> getAllRooms() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room r LEFT JOIN FETCH r.shelves", Room.class)
                         .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Room getRoomByCode(String roomCode) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Room r WHERE r.room_code = :code", Room.class)
                         .setParameter("code", roomCode)
                         .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add the missing getRoomById method
    public Room getRoomById(UUID roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Room> query = session.createQuery(
                "FROM Room r LEFT JOIN FETCH r.shelves WHERE r.room_id = :id", 
                Room.class
            );
            query.setParameter("id", roomId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}