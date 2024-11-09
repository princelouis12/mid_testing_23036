package com.auca.librarymanagement.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.auca.librarymanagement.model.Book;
import com.auca.librarymanagement.model.BookStatus;
import com.auca.librarymanagement.model.Borrower;
import com.auca.librarymanagement.model.Membership;
import com.auca.librarymanagement.model.Shelf;
import com.auca.librarymanagement.model.User;

import util.HibernateUtil;

public class BorrowerDao {
    
    // Add this method for getting all active borrowings
    public List<Borrower> getAllActiveBorrowings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.return_date IS NULL " +
                "ORDER BY b.due_date ASC",
                Borrower.class
            )
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all active borrowings: " + e.getMessage(), e);
        }
    }
    
    

    public List<Borrower> getDueSoonBooks(UUID userId, int daysThreshold) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            
            calendar.add(Calendar.DAY_OF_MONTH, daysThreshold);
            Date thresholdDate = calendar.getTime();

            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.reader.personId = :userId " +
                "AND b.return_date IS NULL " +
                "AND b.due_date BETWEEN :currentDate AND :thresholdDate " +
                "ORDER BY b.due_date ASC",
                Borrower.class
            )
            .setParameter("userId", userId)
            .setParameter("currentDate", currentDate)
            .setParameter("thresholdDate", thresholdDate)
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching due soon books: " + e.getMessage(), e);
        }
    }
    
    public boolean createBorrowing(Book book, User reader, Membership membership) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Set pickup date as current date
            Date pickupDate = new Date();
            
            // Set due date as 14 days from pickup
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pickupDate);
            calendar.add(Calendar.DAY_OF_MONTH, 14);
            Date dueDate = calendar.getTime();
            
            // Create new borrowing record
            Borrower borrower = new Borrower(
                book,
                reader,
                membership,
                pickupDate,
                dueDate
            );
            
            // Save the borrowing record
            session.save(borrower);
            
            // Update book status
            book.setStatus(BookStatus.BORROWED);
            session.update(book);
            
            // Update shelf counts
            Shelf shelf = book.getShelf();
            shelf.setAvailable_stock(shelf.getAvailable_stock() - 1);
            shelf.setBorrowed_number(shelf.getBorrowed_number() + 1);
            session.update(shelf);
            
            transaction.commit();
            return true;
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public List<Borrower> getRecentBorrowings(int limit) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "ORDER BY b.pickup_date DESC",
                Borrower.class
            )
            .setMaxResults(limit)
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching recent borrowings: " + e.getMessage(), e);
        }
    }

    public List<Borrower> getOverdueBorrowings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.return_date IS NULL " +
                "AND b.due_date < CURRENT_DATE " +
                "ORDER BY b.due_date ASC",
                Borrower.class
            )
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching overdue borrowings: " + e.getMessage(), e);
        }
    }

    public List<Borrower> getUserBorrowings(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.reader.personId = :userId " +
                "ORDER BY b.pickup_date DESC",
                Borrower.class
            )
            .setParameter("userId", userId)
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user borrowings: " + e.getMessage(), e);
        }
    }

    public List<Borrower> getActiveBorrowingsByUser(UUID userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.reader.personId = :userId " +
                "AND b.return_date IS NULL",
                Borrower.class
            )
            .setParameter("userId", userId)
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active borrowings: " + e.getMessage(), e);
        }
    }

    public Borrower getBorrowerById(UUID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "WHERE b.id = :id",
                Borrower.class
            )
            .setParameter("id", id)
            .uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching borrower: " + e.getMessage(), e);
        }
    }

    public void createBorrower(Borrower borrower) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(borrower);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error creating borrower: " + e.getMessage(), e);
        }
    }

    public void updateBorrower(Borrower borrower) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(borrower);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating borrower: " + e.getMessage(), e);
        }
    }
    
    public boolean processReturn(UUID borrowerId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Borrower borrower = session.get(Borrower.class, borrowerId);
            if (borrower == null) {
                return false;
            }
            
            // Set return date
            borrower.setReturnDate(new Date());
            
            // Calculate any late fees
            borrower.calculateLateFees();
            
            // Update book status
            Book book = borrower.getBook();
            book.setStatus(BookStatus.AVAILABLE);
            
            // Update shelf counts
            Shelf shelf = book.getShelf();
            shelf.setAvailable_stock(shelf.getAvailable_stock() + 1);
            shelf.setBorrowed_number(shelf.getBorrowed_number() - 1);
            
            session.update(borrower);
            session.update(book);
            session.update(shelf);
            
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public List<Borrower> getAllBorrowings() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Borrower b " +
                "LEFT JOIN FETCH b.book " +
                "LEFT JOIN FETCH b.reader " +
                "LEFT JOIN FETCH b.membership " +
                "ORDER BY b.pickup_date DESC",
                Borrower.class
            )
            .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all borrowings: " + e.getMessage(), e);
        }
    }
}