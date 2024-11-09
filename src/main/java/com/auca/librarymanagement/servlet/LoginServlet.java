package com.auca.librarymanagement.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.auca.librarymanagement.dao.UserDao;
import com.auca.librarymanagement.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDao userDao = new UserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        
        try {
            userDao.findByUsername(userName).ifPresentOrElse(user -> {
                try {
                    if (user.checkPassword(password)) {
                        // Create session and store user
                        HttpSession session = request.getSession(true);
                        session.setAttribute("user", user);
                        
                        // Prepare success response with redirect URL
                        Map<String, String> jsonResponse = new HashMap<>();
                        jsonResponse.put("success", "true");
                        jsonResponse.put("redirect", getRoleBasedRedirectUrl(user));
                        
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        objectMapper.writeValue(response.getWriter(), jsonResponse);
                    } else {
                        sendErrorResponse(response, "Invalid username or password");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, () -> {
                try {
                    sendErrorResponse(response, "Invalid username or password");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            sendErrorResponse(response, "Error during login: " + e.getMessage());
        }
    }
    
    private String getRoleBasedRedirectUrl(User user) {
        switch (user.getRole()) {
            case LIBRARIAN:
                return "librarianDashboard.jsp";  // Changed from librarian/dashboard.jsp
            case STUDENT:
                return "studentDashboard.jsp";    // Changed from student/dashboard.jsp
            case TEACHER:
                return "teacherDashboard.jsp";    // Changed from teacher/dashboard.jsp
            case MANAGER:
            case DEAN:
            case HOD:
                return "adminDashboard.jsp";      // Changed from admin/dashboard.jsp
            default:
                return "dashboard.jsp";
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("success", "false");
        errorResponse.put("error", message);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);  // Changed from UNAUTHORIZED to ensure JS receives the response
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}