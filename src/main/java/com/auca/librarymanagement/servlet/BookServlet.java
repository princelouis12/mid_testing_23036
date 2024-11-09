package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auca.librarymanagement.dao.BookDao;
import com.auca.librarymanagement.dao.ShelfDao;
import com.auca.librarymanagement.model.Book;
import com.auca.librarymanagement.model.BookStatus;
import com.auca.librarymanagement.model.Shelf;


public class BookServlet extends HttpServlet {
 private static final long serialVersionUID = 1L;
 private final BookDao bookDao = new BookDao();
 private final ShelfDao shelfDao = new ShelfDao();
 private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

 @Override
 protected void doGet(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     
     String action = request.getParameter("action");
     
     try {
         if ("search".equals(action)) {
             // Handle search
             String searchTerm = request.getParameter("searchTerm");
             List<Book> books = bookDao.searchBooks(searchTerm);
             request.setAttribute("books", books);
         } else {
             // Default: show all books
             List<Book> books = bookDao.getAllBooks();
             request.setAttribute("books", books);
         }
         
         // Always fetch shelves for the form
         List<Shelf> shelves = shelfDao.getAllShelves();
         request.setAttribute("shelves", shelves);
         
         // Debug print
         System.out.println("Number of shelves found: " + (shelves != null ? shelves.size() : 0));
         
         request.getRequestDispatcher("/manageBooks.jsp").forward(request, response);
         
     } catch (Exception e) {
         System.err.println("Error in BookServlet.doGet(): " + e.getMessage());
         e.printStackTrace();
         request.setAttribute("error", "An error occurred while loading the page");
         request.getRequestDispatcher("/manageBooks.jsp").forward(request, response);
     }
 }

 @Override
 protected void doPost(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     
     String action = request.getParameter("action");
     System.out.println("Received POST request with action: " + action);

     if ("add".equals(action)) {
         handleAddBook(request, response);
     } else if ("edit".equals(action)) {
         handleEditBook(request, response);
     } else if ("delete".equals(action)) {
         handleDeleteBook(request, response);
     } else {
         setError(request, "Invalid action specified", response);
     }
 }
 
 

 private void handleAddBook(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     try {
         // Get form parameters
         String title = request.getParameter("title");
         String isbnCode = request.getParameter("isbnCode");
         String editionStr = request.getParameter("edition");
         String publisherName = request.getParameter("publisherName");
         String shelfIdStr = request.getParameter("shelfId");
         String publicationYearStr = request.getParameter("publicationYear");
         
         // Debug print
         System.out.println("Processing new book:");
         System.out.println("Title: " + title);
         System.out.println("ISBN: " + isbnCode);
         System.out.println("Shelf ID: " + shelfIdStr);
         
         // Validate inputs
         if (!validateInputs(title, isbnCode, editionStr, publisherName, shelfIdStr, publicationYearStr)) {
             setError(request, "All fields are required", response);
             return;
         }
         
         // Parse edition
         int edition = Integer.parseInt(editionStr);
         if (edition <= 0) {
             setError(request, "Edition must be a positive number", response);
             return;
         }
         
         // Get shelf
         UUID shelfId = UUID.fromString(shelfIdStr);
         Shelf shelf = shelfDao.getShelfById(shelfId);
         if (shelf == null) {
             setError(request, "Selected shelf not found", response);
             return;
         }
         
         // Create and save book
         Book book = new Book();
         book.setTitle(title.trim());
         book.setIsbnCode(isbnCode.trim());
         book.setEdition(edition);
         book.setPublisherName(publisherName.trim());
         book.setPublicationYear(dateFormat.parse(publicationYearStr));
         book.setShelf(shelf);
         book.setStatus(BookStatus.AVAILABLE);
         
         if (bookDao.saveBook(book)) {
             System.out.println("Book saved successfully: " + book.getTitle());
             request.setAttribute("success", "Book added successfully");
             response.sendRedirect("BookServlet");
         } else {
             setError(request, "ISBN already exists or error saving book", response);
         }
         
     } catch (ParseException e) {
         setError(request, "Invalid date format", response);
     } catch (NumberFormatException e) {
         setError(request, "Invalid number format", response);
     } catch (IllegalArgumentException e) {
         setError(request, "Invalid input format", response);
     } catch (Exception e) {
         System.err.println("Error saving book: " + e.getMessage());
         e.printStackTrace();
         setError(request, "An error occurred while saving the book", response);
     }
 }

 private boolean validateInputs(String... inputs) {
     for (String input : inputs) {
         if (input == null || input.trim().isEmpty()) {
             return false;
         }
     }
     return true;
 }

 private boolean validateISBN(String isbn) {
     if (isbn == null) return false;
     isbn = isbn.replaceAll("-", "").trim();
     return isbn.matches("^\\d{10}$") || isbn.matches("^\\d{13}$");
 }

 private void handleEditBook(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     // Implement edit functionality if needed
     setError(request, "Edit functionality not implemented yet", response);
 }

 private void handleDeleteBook(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
     // Implement delete functionality if needed
     setError(request, "Delete functionality not implemented yet", response);
 }

 private void setError(HttpServletRequest request, String error, HttpServletResponse response) 
         throws ServletException, IOException {
     request.setAttribute("error", error);
     doGet(request, response);
 }
}