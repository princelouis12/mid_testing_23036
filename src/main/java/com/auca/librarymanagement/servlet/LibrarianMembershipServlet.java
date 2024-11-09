package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.auca.librarymanagement.model.*;
import com.auca.librarymanagement.dao.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/librarian/memberships/*")
public class LibrarianMembershipServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MembershipDao membershipDao = new MembershipDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Check if user is logged in and is a librarian
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            if (!currentUser.isLibrarian()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            // Get all memberships
            List<Membership> memberships = membershipDao.getAllMemberships();
            
            // Calculate pending count
            long pendingCount = memberships.stream()
                .filter(m -> m.getStatus() == Status.PENDING)
                .count();
            
            request.setAttribute("memberships", memberships);
            request.setAttribute("pendingCount", pendingCount);
            
            request.getRequestDispatcher("/manageMemberships.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Check if user is logged in and is a librarian
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                sendError(response, "User not logged in");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            if (!currentUser.isLibrarian()) {
                sendError(response, "Access denied");
                return;
            }

            String pathInfo = request.getPathInfo();
            String membershipId = request.getParameter("membershipId");
            
            if (membershipId == null || membershipId.trim().isEmpty()) {
                sendError(response, "Membership ID is required");
                return;
            }

            Membership membership = membershipDao.getMembershipById(UUID.fromString(membershipId));
            if (membership == null) {
                sendError(response, "Membership not found");
                return;
            }

            if ("/approve".equals(pathInfo)) {
                membership.setStatus(Status.APPROVED);
                membershipDao.updateMembership(membership);
                sendSuccess(response, "Membership approved successfully");
            } else if ("/deny".equals(pathInfo)) {
                membership.setStatus(Status.REJECTED);
                membershipDao.updateMembership(membership);
                sendSuccess(response, "Membership denied successfully");
            } else {
                sendError(response, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error processing request: " + e.getMessage());
        }
    }

    private void sendSuccess(HttpServletResponse response, String message) throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put("success", "true");
        result.put("message", message);
        
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), result);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put("success", "false");
        result.put("error", message);
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(response.getWriter(), result);
    }
}