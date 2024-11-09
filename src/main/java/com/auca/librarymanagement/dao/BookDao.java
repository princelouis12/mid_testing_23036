package com.auca.librarymanagement.dao;

import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.auca.librarymanagement.model.Book;
import com.auca.librarymanagement.model.BookStatus;
import com.auca.librarymanagement.model.Shelf;
import util.HibernateUtil;

public class BookDao {
    
    public boolean saveBook(Book book) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // Check if ISBN already exists
            if (getBookByIsbn(book.getIsbnCode()) != null) {
                return false;
            }
            
            // Update shelf counts
            Shelf shelf = book.getShelf();
            shelf.setAvailable_stock(shelf.getAvailable_stock() + 1);
            shelf.setInitial_stock(shelf.getInitial_stock() + 1);
            
            session.update(shelf);
            session.save(book);
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
    
    public int getTotalBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(b) FROM Book b", Long.class)
                         .uniqueResult()
                         .intValue();
        }
    }
    
    public void save(Book book) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(book);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving book: " + e.getMessage(), e);
        }
    }
    
    public List<Book> getAllBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Book b LEFT JOIN FETCH b.shelf s LEFT JOIN FETCH s.room ORDER BY b.title", 
                Book.class
            ).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Book> getAvailableBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Book b LEFT JOIN FETCH b.shelf s LEFT JOIN FETCH s.room " +
                "WHERE b.status = :status ORDER BY b.title", 
                Book.class
            )
            .setParameter("status", BookStatus.AVAILABLE)
            .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void updateBook(Book book) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(book);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating book: " + e.getMessage(), e);
        }
    }
    
    public Book getBookById(UUID bookId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Book.class, bookId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching book: " + e.getMessage(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAllCategories() {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Using createQuery with explicit cast
            String hql = "SELECT DISTINCT s.book_category FROM Shelf s WHERE s.book_category IS NOT NULL ORDER BY s.book_category";
            return session.createQuery(hql).list();
            
        } catch (Exception e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching book categories", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    public Book getBookByIsbn(String isbn) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Book b WHERE b.isbnCode = :isbn",
                Book.class
            )
            .setParameter("isbn", isbn)
            .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Book> searchBooks(String query) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Book b LEFT JOIN FETCH b.shelf s LEFT JOIN FETCH s.room " +
                "WHERE lower(b.title) LIKE lower(:query) " +
                "OR lower(b.isbnCode) LIKE lower(:query) " +
                "OR lower(s.book_category) LIKE lower(:query) " +
                "ORDER BY b.title",
                Book.class
            )
            .setParameter("query", "%" + query + "%")
            .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getTotalBorrowedBooks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT COUNT(b) FROM Book b WHERE b.status = :status", 
                Long.class
            )
            .setParameter("status", BookStatus.BORROWED)
            .uniqueResult()
            .intValue();
        }
    }
    
    public boolean updateBookStatus(UUID bookId, BookStatus status) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            Book book = getBookById(bookId);
            if (book != null) {
                book.setStatus(status);
                session.update(book);
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