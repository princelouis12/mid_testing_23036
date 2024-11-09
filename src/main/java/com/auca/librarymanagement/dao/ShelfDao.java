// ShelfDao.java
package com.auca.librarymanagement.dao;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.auca.librarymanagement.model.Shelf;

import util.HibernateUtil;

public class ShelfDao {
    
	public List<Shelf> getAllShelves() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Debug print
            System.out.println("Fetching all shelves...");
            
            // Modified query with explicit joins
            String hql = "SELECT DISTINCT s FROM Shelf s " +
                        "LEFT JOIN FETCH s.room r " +
                        "WHERE s.shelf_id IS NOT NULL " +
                        "ORDER BY r.room_code, s.book_category";
            
            List<Shelf> shelves = session.createQuery(hql, Shelf.class)
                                       .getResultList();
            
            // Debug print
            System.out.println("Found " + shelves.size() + " shelves");
            for (Shelf shelf : shelves) {
                System.out.println("Shelf ID: " + shelf.getShelf_id() + 
                                 ", Category: " + shelf.getBook_category() +
                                 ", Room: " + (shelf.getRoom() != null ? 
                                             shelf.getRoom().getRoom_code() : "null"));
            }
            
            return shelves;
        } catch (Exception e) {
            System.err.println("Error fetching shelves: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

	public Shelf getShelfById(UUID shelfId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Shelf s LEFT JOIN FETCH s.room WHERE s.shelf_id = :id",
                Shelf.class
            )
            .setParameter("id", shelfId)
            .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean saveShelf(Shelf shelf) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Check if the category already exists in the room
            List<Shelf> existingShelves = getShelfByRoomAndCategory(
                shelf.getRoom().getRoom_id(), 
                shelf.getBook_category()
            );
            
            if (!existingShelves.isEmpty()) {
                return false;
            }
            
            session.save(shelf);
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
    
    public List<Shelf> getShelfByRoomAndCategory(UUID roomId, String category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Shelf s WHERE s.room.room_id = :roomId AND s.book_category = :category";
            return session.createQuery(hql, Shelf.class)
                         .setParameter("roomId", roomId)
                         .setParameter("category", category)
                         .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Shelf> getShelvesForRoom(UUID roomId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Shelf s WHERE s.room.room_id = :roomId ORDER BY s.book_category";
            return session.createQuery(hql, Shelf.class)
                         .setParameter("roomId", roomId)
                         .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateShelfStock(UUID shelfId, int newStock) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Shelf shelf = session.get(Shelf.class, shelfId);
            if (shelf != null) {
                shelf.setInitial_stock(newStock);
                shelf.setAvailable_stock(newStock - shelf.getBorrowed_number());
                session.update(shelf);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean incrementBorrowedCount(UUID shelfId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Shelf shelf = session.get(Shelf.class, shelfId);
            if (shelf != null && shelf.getAvailable_stock() > 0) {
                shelf.setBorrowed_number(shelf.getBorrowed_number() + 1);
                shelf.setAvailable_stock(shelf.getAvailable_stock() - 1);
                session.update(shelf);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean decrementBorrowedCount(UUID shelfId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Shelf shelf = session.get(Shelf.class, shelfId);
            if (shelf != null && shelf.getBorrowed_number() > 0) {
                shelf.setBorrowed_number(shelf.getBorrowed_number() - 1);
                shelf.setAvailable_stock(shelf.getAvailable_stock() + 1);
                session.update(shelf);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}