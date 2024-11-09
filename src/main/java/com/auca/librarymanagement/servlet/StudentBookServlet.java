package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auca.librarymanagement.dao.BookDao;
import com.auca.librarymanagement.dao.MembershipDao;
import com.auca.librarymanagement.dao.BorrowerDao;
import com.auca.librarymanagement.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/student/books/*")
public class StudentBookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final BookDao bookDao = new BookDao();
    private final MembershipDao membershipDao = new MembershipDao();
    private final BorrowerDao borrowerDao = new BorrowerDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ... other methods remain the same ...

    private void handleBorrowBook(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get user from session
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                handleError(response, "User not logged in", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            
            User user = (User) session.getAttribute("user");
            String bookIdStr = request.getParameter("bookId");
            
            if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
                handleError(response, "Book ID is required");
                return;
            }
            
            UUID bookId = UUID.fromString(bookIdStr);
            
            // Verify active membership using Optional
            Optional<Membership> activeMembershipOpt = membershipDao.getActiveMembership(user.getPersonId());
            
            if (activeMembershipOpt.isEmpty()) {
                handleError(response, "No active membership found");
                return;
            }
            
            Membership activeMembership = activeMembershipOpt.get();
            
            // Check if user can borrow more books
            if (!activeMembership.canBorrowMore()) {
                handleError(response, "Maximum books limit reached for your membership");
                return;
            }
            
            // Get the book
            Book book = bookDao.getBookById(bookId);
            if (book == null) {
                handleError(response, "Book not found");
                return;
            }
            
            if (book.getStatus() != BookStatus.AVAILABLE) {
                handleError(response, "Book is not available for borrowing");
                return;
            }
            
            // Create borrowing record
            if (borrowerDao.createBorrowing(book, user, activeMembership)) {
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("success", "true");
                successResponse.put("message", "Book borrowed successfully");
                
                response.setContentType("application/json");
                objectMapper.writeValue(response.getWriter(), successResponse);
            } else {
                handleError(response, "Failed to create borrowing record");
            }
            
        } catch (IllegalArgumentException e) {
            handleError(response, "Invalid book ID format");
        } catch (Exception e) {
            handleError(response, "Error processing borrow request: " + e.getMessage());
        }
    }

    private void handleListBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Book> books = bookDao.getAllBooks();
            request.setAttribute("books", books);
            
            // Also add membership status for UI handling
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            Optional<Membership> activeMembership = membershipDao.getActiveMembership(user.getPersonId());
            request.setAttribute("activeMembership", activeMembership.orElse(null));
            
            request.getRequestDispatcher("/WEB-INF/views/student/bookList.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            handleError(response, "Error retrieving books: " + e.getMessage());
        }
    }

    private void handleListAvailableBooks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("Handling list available books request...");
            
            List<Book> availableBooks = bookDao.getAvailableBooks();
            System.out.println("Retrieved " + (availableBooks != null ? availableBooks.size() : "null") + " books");
            
            request.setAttribute("books", availableBooks);
            
            // Add membership status for UI handling
            HttpSession session = request.getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    Optional<Membership> activeMembership = membershipDao.getActiveMembership(user.getPersonId());
                    request.setAttribute("activeMembership", activeMembership.orElse(null));
                }
            }
            
            // Make sure this path matches your project structure
            String jspPath = "/availableBooks.jsp";
            System.out.println("Forwarding to JSP: " + jspPath);
            request.getRequestDispatcher(jspPath).forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in handleListAvailableBooks: " + e.getMessage());
            e.printStackTrace();
            handleError(response, "Error retrieving available books: " + e.getMessage());
        }
    }
    
    private void handleError(HttpServletResponse response, String message) throws IOException {
        handleError(response, message, HttpServletResponse.SC_BAD_REQUEST);
    }
    
    private void handleError(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("success", "false");
        errorResponse.put("error", message);
        
        response.setContentType("application/json");
        response.setStatus(statusCode);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}