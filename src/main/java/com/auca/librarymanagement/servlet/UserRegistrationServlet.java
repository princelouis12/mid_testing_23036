package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auca.librarymanagement.dao.UserDao;
import com.auca.librarymanagement.model.Gender;
import com.auca.librarymanagement.model.Location;
import com.auca.librarymanagement.model.LocationType;
import com.auca.librarymanagement.model.RoleType;
import com.auca.librarymanagement.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class UserRegistrationServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UserDao userDao = new UserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get and validate form parameters
            validateAndProcessRegistration(request, response);
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void validateAndProcessRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        // Get form parameters
        String firstName = getRequiredParameter(request, "firstName");
        String lastName = getRequiredParameter(request, "lastName");
        String userName = getRequiredParameter(request, "userName");
        String password = getRequiredParameter(request, "password");
        String phoneNumber = getRequiredParameter(request, "phoneNumber");
        String villageId = getRequiredParameter(request, "villageId");
        
        // Parse enums
        Gender gender = Gender.valueOf(getRequiredParameter(request, "gender"));
        RoleType role = RoleType.valueOf(getRequiredParameter(request, "role"));
        
        // Validate username format
        validateUsername(userName);
        
        // Validate password
        validatePassword(password);
        
        // Check if username exists
        if (userDao.existsByUsername(userName)) {
            sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Username already exists");
            return;
        }
        
        // Find and validate village
        Location village = userDao.findVillageById(UUID.fromString(villageId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid village ID"));
        
        validateVillage(village);

        // Create and save user
        User user = new User(
            UUID.randomUUID(),
            firstName,
            lastName,
            gender,
            phoneNumber,
            userName,
            password,
            role,
            village
        );
        
        userDao.createUser(user);
        
        // Send success response
        sendSuccessResponse(response);
    }
    
    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field: " + paramName);
        }
        return value.trim();
    }
    
    private void validateUsername(String userName) {
        if (!userName.matches("^[a-zA-Z0-9_]{4,50}$")) {
            throw new IllegalArgumentException(
                "Username must be 4-50 characters and contain only letters, numbers, and underscores"
            );
        }
    }
    
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
    
    private void validateVillage(Location village) {
        if (village.getLocationType() != LocationType.VILLAGE) {
            throw new IllegalArgumentException("Selected location must be a village");
        }
    }
    
    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Registration successful");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), successResponse);
    }
    
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) 
            throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } else if (e instanceof IllegalStateException) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } else {
            e.printStackTrace();
            sendErrorResponse(
                response, 
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing registration"
            );
        }
    }
}