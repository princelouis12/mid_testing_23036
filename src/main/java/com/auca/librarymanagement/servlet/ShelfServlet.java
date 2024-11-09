package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auca.librarymanagement.dao.RoomDao;
import com.auca.librarymanagement.dao.ShelfDao;
import com.auca.librarymanagement.model.Room;
import com.auca.librarymanagement.model.Shelf;


public class ShelfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ShelfDao shelfDao = new ShelfDao();
    private final RoomDao roomDao = new RoomDao();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roomId = request.getParameter("roomId");
        String bookCategory = request.getParameter("bookCategory");
        String initialStockStr = request.getParameter("initialStock");
        
        // Validate inputs
        if (roomId == null || bookCategory == null || initialStockStr == null || 
            bookCategory.trim().isEmpty() || initialStockStr.trim().isEmpty()) {
            setError(request, "All fields are required", response);
            return;
        }
        
        // Validate category format
        if (!bookCategory.matches("^[A-Za-z0-9\\s-]{3,50}$")) {
            setError(request, "Invalid category format. Use letters, numbers, spaces and hyphens (3-50 characters)", response);
            return;
        }
        
        // Validate initial stock
        int initialStock;
        try {
            initialStock = Integer.parseInt(initialStockStr);
            if (initialStock < 0) {
                setError(request, "Initial stock cannot be negative", response);
                return;
            }
        } catch (NumberFormatException e) {
            setError(request, "Invalid initial stock number", response);
            return;
        }
        
        try {
            // Get the room
            Room room = roomDao.getRoomById(UUID.fromString(roomId));
            if (room == null) {
                setError(request, "Room not found", response);
                return;
            }
            
            // Create and save new shelf
            Shelf shelf = new Shelf(bookCategory.trim(), room, initialStock);
            if (shelfDao.saveShelf(shelf)) {
                response.sendRedirect("RoomServlet");
            } else {
                setError(request, "Category already exists in this room or there was an error saving the shelf", response);
            }
        } catch (IllegalArgumentException e) {
            setError(request, "Invalid room ID format", response);
        }
    }
    
    private void setError(HttpServletRequest request, String error, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.getRequestDispatcher("RoomServlet").forward(request, response);
    }
}