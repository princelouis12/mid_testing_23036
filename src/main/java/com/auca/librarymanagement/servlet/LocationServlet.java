package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.auca.librarymanagement.dao.LocationDao;
import com.auca.librarymanagement.model.Location;
import com.auca.librarymanagement.model.LocationType;

public class LocationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LocationDao locationDao = new LocationDao();
    private final Gson gson;

    public LocationServlet() {
        // Configure Gson to handle circular references
        this.gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // Skip parent and children fields to prevent circular references
                    return f.getName().equals("parent") || 
                           f.getName().equals("children") || 
                           f.getName().equals("users");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .serializeNulls()
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "getLocations";
        }
        
        try {
            switch (action) {
                case "getLocations":
                    handleGetLocations(request, response);
                    break;
                case "getChildren":
                    handleGetChildren(request, response);
                    break;
                case "edit":
                    handleEdit(request, response);
                    break;
                default:
                    handleGetLocations(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, e);
        }
        
        if ("debug".equals(request.getParameter("action"))) {
            locationDao.verifyLocationHierarchy();
            String locationId = request.getParameter("locationId");
            if (locationId != null) {
                locationDao.debugLocationHierarchy(UUID.fromString(locationId));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        try {
            switch (action) {
                case "save":
                    handleSave(request, response);
                    break;
                case "delete":
                    handleDelete(request, response);
                    break;
                default:
                    handleSave(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            handleError(request, response, e);
        }
    }

    private void handleGetLocations(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Location> provinces = locationDao.getLocationsByType(LocationType.PROVINCE);
            List<Location> allLocations = locationDao.getAllLocations();
            
            System.out.println("Loaded provinces: " + provinces.size());
            for (Location province : provinces) {
                System.out.println("Province: " + province.getLocationName() + " (ID: " + province.getLocationId() + ")");
            }
            
            request.setAttribute("provinces", provinces);
            request.setAttribute("locations", allLocations);
            request.getRequestDispatcher("/locationManagement.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error loading locations", e);
        }
    }

    private void handleGetChildren(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String parentId = request.getParameter("parentId");
            System.out.println("\n=== handleGetChildren - Start ===");
            System.out.println("Received parentId: " + parentId);

            if (parentId == null || parentId.trim().isEmpty()) {
                throw new IllegalArgumentException("Parent ID is required");
            }

            UUID uuid = UUID.fromString(parentId.trim());
            System.out.println("Valid UUID format: " + uuid);
            
            // Get parent location first
            Location parent = locationDao.getLocationById(uuid);
            if (parent == null) {
                throw new IllegalArgumentException("Parent location not found for ID: " + uuid);
            }
            System.out.println("Found parent: " + parent.getLocationName() + 
                             " (Type: " + parent.getLocationType() + ")");

            // Get children
            List<Location> children = locationDao.getChildLocations(uuid);
            System.out.println("Found " + children.size() + " children");

            // Create response data
            List<Map<String, String>> responseData = new ArrayList<>();
            for (Location child : children) {
                Map<String, String> childData = new HashMap<>();
                childData.put("locationId", child.getLocationId().toString());
                childData.put("locationName", child.getLocationName());
                childData.put("locationCode", child.getLocationCode());
                responseData.add(childData);
                
                System.out.println("Added to response: " + child.getLocationName() + 
                                 " (" + child.getLocationCode() + ")");
            }

            // Convert to JSON and send response
            String jsonResponse = new Gson().toJson(responseData);
            System.out.println("Sending JSON response: " + jsonResponse);
            out.print(jsonResponse);
            out.flush();

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid request error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid request: " + e.getMessage());
            out.print(new Gson().toJson(errorResponse));
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Server error: " + e.getMessage());
            out.print(new Gson().toJson(errorResponse));
        } finally {
            out.close();
        }
        System.out.println("=== handleGetChildren - End ===\n");
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(errorResponse));
            out.flush();
        }
    }

    

    private void handleEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String locationId = request.getParameter("id");
        if (locationId != null && !locationId.isEmpty()) {
            try {
                Location location = locationDao.getLocationById(UUID.fromString(locationId));
                if (location != null) {
                    request.setAttribute("editLocation", location);
                    
                    // If editing a non-province location, load its parent hierarchy
                    if (location.getParent() != null) {
                        Location parent = location.getParent();
                        request.setAttribute("selectedParent", parent);
                        
                        // Load the complete parent hierarchy
                        List<Location> parentHierarchy = new ArrayList<>();
                        while (parent != null) {
                            parentHierarchy.add(0, parent);
                            parent = parent.getParent();
                        }
                        request.setAttribute("parentHierarchy", parentHierarchy);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading location for editing: " + e.getMessage());
                e.printStackTrace();
            }
        }
        handleGetLocations(request, response);
    }

    private void handleSave(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            Location location = createLocationFromRequest(request);
            
            // Add additional validation
            if (location.getLocationName() == null || location.getLocationName().trim().isEmpty()) {
                throw new IllegalArgumentException("Location name is required");
            }
            if (location.getLocationCode() == null || location.getLocationCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Location code is required");
            }
            
            locationDao.saveLocation(location);
            System.out.println("Successfully saved location: " + location.getLocationName());
            
            response.sendRedirect(request.getContextPath() + "/LocationServlet?action=getLocations");
        } catch (Exception e) {
            System.err.println("Error saving location: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error saving location: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            String locationId = request.getParameter("locationId");
            if (locationId != null && !locationId.isEmpty()) {
                locationDao.deleteLocation(UUID.fromString(locationId));
                response.setStatus(HttpServletResponse.SC_OK);
                System.out.println("Successfully deleted location with ID: " + locationId);
            } else {
                throw new IllegalArgumentException("Location ID is required");
            }
        } catch (Exception e) {
            System.err.println("Error deleting location: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error deleting location: " + e.getMessage());
        }
    }

    private Location createLocationFromRequest(HttpServletRequest request) {
        try {
            System.out.println("\n=== Creating Location From Request ===");
            
            // Log all request parameters for debugging
            System.out.println("Request Parameters:");
            request.getParameterMap().forEach((key, value) -> {
                System.out.println(key + ": " + Arrays.toString(value));
            });

            Location location = new Location();
            
            // Get and validate required parameters
            String locationType = request.getParameter("locationType");
            String locationCode = request.getParameter("locationCode");
            String locationName = request.getParameter("locationName");
            
            System.out.println("\nBasic Location Info:");
            System.out.println("Type: " + locationType);
            System.out.println("Code: " + locationCode);
            System.out.println("Name: " + locationName);

            if (locationType == null || locationCode == null || locationName == null) {
                throw new IllegalArgumentException("Missing required location information");
            }

            location.setLocationType(LocationType.valueOf(locationType));
            location.setLocationCode(locationCode);
            location.setLocationName(locationName);
            
            // Handle parent location based on type
            String parentId = null;
            switch (LocationType.valueOf(locationType)) {
                case DISTRICT -> parentId = request.getParameter("provinceId");
                case SECTOR -> parentId = request.getParameter("districtId");
                case CELL -> parentId = request.getParameter("sectorId");
                case VILLAGE -> parentId = request.getParameter("cellId");
                case PROVINCE -> System.out.println("Province has no parent");
            }
            
            System.out.println("\nParent Location Info:");
            System.out.println("Parent ID from request: " + parentId);

            // Set parent if required
            if (parentId != null && !parentId.isEmpty()) {
                try {
                    UUID parentUUID = UUID.fromString(parentId.trim());
                    System.out.println("Parsed Parent UUID: " + parentUUID);
                    
                    // Get parent location
                    Location parent = locationDao.getLocationById(parentUUID);
                    
                    if (parent == null) {
                        System.err.println("Parent location not found in database: " + parentUUID);
                        throw new IllegalArgumentException(
                            location.getLocationType().toString() + " parent not found: " + parentUUID);
                    }
                    
                    System.out.println("Found Parent: " + parent.getLocationName() + 
                                     " (Type: " + parent.getLocationType() + 
                                     ", ID: " + parent.getLocationId() + ")");
                    
                    location.setParent(parent);
                    
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid parent UUID format: " + parentId);
                    throw new IllegalArgumentException("Invalid parent location ID format", e);
                }
            } else if (location.getLocationType() != LocationType.PROVINCE) {
                throw new IllegalArgumentException(
                    location.getLocationType().toString() + " must have a parent location");
            }

            System.out.println("\nFinal Location Object:");
            System.out.println("Name: " + location.getLocationName());
            System.out.println("Type: " + location.getLocationType());
            System.out.println("Code: " + location.getLocationCode());
            System.out.println("Parent: " + (location.getParent() != null ? 
                location.getParent().getLocationName() + " (" + location.getParent().getLocationId() + ")" : 
                "none"));
            
            System.out.println("=== Location Creation Complete ===\n");
            
            return location;
            
        } catch (Exception e) {
            System.err.println("Error creating location from request: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) 
            throws ServletException, IOException {
        System.err.println("Error handling request: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", e.getMessage());
        handleGetLocations(request, response);
    }

    private static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
}