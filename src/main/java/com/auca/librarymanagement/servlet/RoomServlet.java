package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auca.librarymanagement.dao.RoomDao;
import com.auca.librarymanagement.model.Room;

public class RoomServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final RoomDao roomDao = new RoomDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Room> rooms = roomDao.getAllRooms();
        request.setAttribute("rooms", rooms);
        request.getRequestDispatcher("/manageRooms.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roomCode = request.getParameter("roomCode");
        
        // Basic validation
        if (roomCode == null || roomCode.trim().isEmpty() || 
            !roomCode.matches("^[A-Za-z0-9]{3,10}$")) {
            request.setAttribute("error", "Invalid room code format. Code should be 3-10 alphanumeric characters.");
            doGet(request, response);
            return;
        }
        
        // Create and save room
        Room room = new Room(roomCode.trim());
        if (roomDao.saveRoom(room)) {
            response.sendRedirect("RoomServlet");
        } else {
            request.setAttribute("error", "Room code already exists or there was an error saving the room.");
            doGet(request, response);
        }
    }
}