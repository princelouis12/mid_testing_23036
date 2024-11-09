package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.auca.librarymanagement.dao.UserDao;
import com.auca.librarymanagement.model.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonProperty;

@WebServlet("/getVillages")
public class VillageServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final UserDao userDao = new UserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String provinceId = request.getParameter("provinceId");
            List<Location> villages;
            
            if (provinceId != null && !provinceId.isEmpty()) {
                villages = userDao.findVillagesByProvince(UUID.fromString(provinceId));
            } else {
                villages = userDao.findAllVillages();
            }
            
            // Convert to DTO to avoid serialization issues
            List<VillageDto> villageDtos = villages.stream()
                .map(village -> new VillageDto(
                    village.getLocationId().toString(),
                    userDao.getLocationHierarchy(village)
                ))
                .collect(Collectors.toList());
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), villageDtos);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), 
                new ErrorResponse("Error loading villages: " + e.getMessage()));
        }
    }
    
    // Static nested class for village data transfer
    public static class VillageDto {
        @JsonProperty("id")
        private final String locationId;
        
        @JsonProperty("name")
        private final String hierarchy;
        
        public VillageDto(String locationId, String hierarchy) {
            this.locationId = locationId;
            this.hierarchy = hierarchy;
        }
        
        public String getLocationId() {
            return locationId;
        }
        
        public String getHierarchy() {
            return hierarchy;
        }
    }
    
    // Static nested class for error responses
    public static class ErrorResponse {
        @JsonProperty("error")
        private final String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
}