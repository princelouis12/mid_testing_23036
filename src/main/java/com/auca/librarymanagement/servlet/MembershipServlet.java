package com.auca.librarymanagement.servlet;

import java.io.IOException;
import java.util.*;

import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.auca.librarymanagement.model.*;
import com.auca.librarymanagement.dao.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import util.HibernateUtil;

@WebServlet("/membership/*")
public class MembershipServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MembershipDao membershipDao = new MembershipDao();
    private final MembershipTypeDao membershipTypeDao = new MembershipTypeDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            System.out.println("\n=== Starting MembershipServlet doGet ===");
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                System.out.println("No valid session found - redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            System.out.println("Current user: " + currentUser.getFirstName() + 
                " " + currentUser.getLastName() + 
                " (Librarian: " + currentUser.isLibrarian() + ")");
            
            if (currentUser.isLibrarian()) {
                List<Membership> memberships = membershipDao.getAllMemberships();
                System.out.println("Retrieved " + 
                    (memberships != null ? memberships.size() : "null") + 
                    " memberships");
                
                request.setAttribute("memberships", memberships);
                System.out.println("Set memberships attribute in request");
                
                String jspPath = "/manageMemberships.jsp";
                System.out.println("Forwarding to JSP: " + jspPath);
                request.getRequestDispatcher(jspPath).forward(request, response);
                
            } else {
                // For student view, load all necessary membership data
                UUID userId = currentUser.getPersonId();
                
                // Get active membership
                Membership activeMembership = membershipDao.getActiveMembershipForUser(userId);
                session.setAttribute("activeMembership", activeMembership);
                
                // Get pending memberships if no active membership
                if (activeMembership == null) {
                    List<Membership> pendingMemberships = membershipDao.getMembershipsByStatus(Status.PENDING);
                    pendingMemberships.removeIf(m -> !m.getUser().getPersonId().equals(userId));
                    session.setAttribute("pendingMemberships", pendingMemberships);
                    
                    // Check for rejected memberships
                    List<Membership> rejectedMemberships = membershipDao.getMembershipsByStatus(Status.REJECTED);
                    rejectedMemberships.removeIf(m -> !m.getUser().getPersonId().equals(userId));
                    if (!rejectedMemberships.isEmpty()) {
                        session.setAttribute("rejectedMembership", rejectedMemberships.get(0));
                    }
                }
                
                // Get all memberships for history
                List<Membership> allMemberships = membershipDao.getMembershipsByUser(userId);
                session.setAttribute("allMemberships", allMemberships);
                
                // Get borrowed books count
                int borrowedBooksCount = membershipDao.getCurrentBorrowingsCount(userId);
                session.setAttribute("borrowedBooksCount", borrowedBooksCount);
                
                // Get unpaid fines
                int unpaidFines = membershipDao.getUnpaidFines(userId);
                session.setAttribute("unpaidFines", unpaidFines);
                
                System.out.println("Non-librarian user - redirecting to dashboard");
                response.sendRedirect(request.getContextPath() + "/studentDashboard");
            }
            
        } catch (Exception e) {
            System.err.println("Error in doGet: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error processing request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            handleNewMembershipRequest(request, response);
        } else if (pathInfo.equals("/approve")) {
            handleMembershipApproval(request, response);
        } else if (pathInfo.equals("/deny")) {
            handleMembershipDenial(request, response);
        }
    }

    private void handleNewMembershipRequest(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");
            String membershipTypeId = request.getParameter("membershipTypeId");
            
            // Validate user can apply for membership
            if (user == null) {
                sendError(response, "User session not found");
                return;
            }
            
            if (membershipDao.hasUnpaidFines(user.getPersonId())) {
                sendError(response, "Cannot apply for membership due to unpaid fines");
                return;
            }

            // Validate membership type
            if (membershipTypeId == null || membershipTypeId.trim().isEmpty()) {
                sendError(response, "Invalid membership type selected");
                return;
            }

            MembershipType membershipType = membershipTypeDao.getMembershipTypeById(
                UUID.fromString(membershipTypeId)
            );
            
            if (membershipType == null) {
                sendError(response, "Selected membership type not found");
                return;
            }

            // Calculate dates
            Date registrationDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(registrationDate);
            calendar.add(Calendar.MONTH, 6);
            Date expiryDate = calendar.getTime();

            // Generate membership code
            String membershipCode = generateMembershipCode(user);
            if (membershipDao.existsByMembershipCode(membershipCode)) {
                membershipCode = generateMembershipCode(user);
            }

            // Create new membership
            Membership membership = new Membership(
                membershipCode,
                membershipType,
                user,
                registrationDate,
                expiryDate
            );
            membership.setStatus(Status.PENDING);

            membershipDao.createMembership(membership);
            
            // Update session attributes
            List<Membership> pendingMemberships = (List<Membership>) session.getAttribute("pendingMemberships");
            if (pendingMemberships == null) {
                pendingMemberships = new ArrayList<>();
            }
            pendingMemberships.add(membership);
            session.setAttribute("pendingMemberships", pendingMemberships);
            
            // Add status update notification
            Map<String, String> statusUpdate = new HashMap<>();
            statusUpdate.put("title", "Application Submitted");
            statusUpdate.put("message", "Your membership application has been submitted successfully.");
            statusUpdate.put("status", "success");
            session.setAttribute("membershipStatusUpdate", statusUpdate);
            
            Map<String, String> responseData = new HashMap<>();
            responseData.put("success", "true");
            responseData.put("message", "Membership application submitted successfully");
            
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), responseData);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error creating membership: " + e.getMessage());
        }
    }

    private void handleMembershipApproval(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("user");
            
            if (!currentUser.isLibrarian()) {
                sendError(response, "Unauthorized access");
                return;
            }

            String membershipId = request.getParameter("membershipId");
            if (membershipId == null || membershipId.trim().isEmpty()) {
                sendError(response, "Invalid membership ID");
                return;
            }

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();
            
            try {
                String hql = "SELECT m FROM Membership m " +
                            "LEFT JOIN FETCH m.membershipType " +
                            "LEFT JOIN FETCH m.user " +
                            "WHERE m.membershipId = :id";
                
                TypedQuery<Membership> query = hibernateSession.createQuery(hql, Membership.class);
                query.setParameter("id", UUID.fromString(membershipId));
                
                Membership membership = query.getSingleResult();
                
                if (membership != null) {
                    membership.setStatus(Status.APPROVED);
                    hibernateSession.merge(membership);
                    transaction.commit();
                    
                    // Update session attributes for the student's session
                    HttpSession studentSession = findStudentSession(membership.getUser().getPersonId());
                    if (studentSession != null) {
                        studentSession.setAttribute("activeMembership", membership);
                        studentSession.removeAttribute("pendingMemberships");
                        studentSession.removeAttribute("rejectedMembership");
                        
                        // Add status update notification
                        Map<String, String> statusUpdate = new HashMap<>();
                        statusUpdate.put("title", "Membership Approved");
                        statusUpdate.put("message", "Your membership application has been approved!");
                        statusUpdate.put("status", "success");
                        studentSession.setAttribute("membershipStatusUpdate", statusUpdate);
                    }
                    
                    Map<String, String> responseData = new HashMap<>();
                    responseData.put("success", "true");
                    responseData.put("message", "Membership approved successfully");
                    
                    response.setContentType("application/json");
                    objectMapper.writeValue(response.getWriter(), responseData);
                } else {
                    sendError(response, "Membership not found");
                }
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                if (hibernateSession != null && hibernateSession.isOpen()) {
                    hibernateSession.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in handleMembershipApproval: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error approving membership: " + e.getMessage());
        }
    }

    private void handleMembershipDenial(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            User currentUser = (User) session.getAttribute("user");
            
            if (!currentUser.isLibrarian()) {
                sendError(response, "Unauthorized access");
                return;
            }

            String membershipId = request.getParameter("membershipId");
            if (membershipId == null || membershipId.trim().isEmpty()) {
                sendError(response, "Invalid membership ID");
                return;
            }

            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = hibernateSession.beginTransaction();
            
            try {
                String hql = "SELECT m FROM Membership m " +
                            "LEFT JOIN FETCH m.membershipType " +
                            "LEFT JOIN FETCH m.user " +
                            "WHERE m.membershipId = :id";
                
                TypedQuery<Membership> query = hibernateSession.createQuery(hql, Membership.class);
                query.setParameter("id", UUID.fromString(membershipId));
                
                Membership membership = query.getSingleResult();
                
                if (membership != null) {
                    membership.setStatus(Status.REJECTED);
                    hibernateSession.merge(membership);
                    transaction.commit();
                    
                    // Update session attributes for the student's session
                    HttpSession studentSession = findStudentSession(membership.getUser().getPersonId());
                    if (studentSession != null) {
                        studentSession.removeAttribute("pendingMemberships");
                        studentSession.setAttribute("rejectedMembership", membership);
                        
                        // Add status update notification
                        Map<String, String> statusUpdate = new HashMap<>();
                        statusUpdate.put("title", "Membership Application Rejected");
                        statusUpdate.put("message", "Your membership application has been rejected. You may apply again after 30 days.");
                        statusUpdate.put("status", "error");
                        studentSession.setAttribute("membershipStatusUpdate", statusUpdate);
                    }
                    
                    Map<String, String> responseData = new HashMap<>();
                    responseData.put("success", "true");
                    responseData.put("message", "Membership denied successfully");
                    
                    response.setContentType("application/json");
                    objectMapper.writeValue(response.getWriter(), responseData);
                } else {
                    sendError(response, "Membership not found");
                }
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            } finally {
                if (hibernateSession != null && hibernateSession.isOpen()) {
                    hibernateSession.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in handleMembershipDenial: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error denying membership: " + e.getMessage());
        }
    }

    private HttpSession findStudentSession(UUID userId) {
        // Note: This is a placeholder. In a production environment,
        // you would need to implement session management to track user sessions
        return null;
    }

    private boolean isValidMembershipName(String name) {
        if (name == null) return false;
        String upperName = name.trim().toUpperCase();
        return upperName.equals("GOLD") || upperName.equals("SILVER") || upperName.equals("STRIVER");
    }

    private int getMembershipMaxBooks(String membershipName) {
        switch(membershipName.toUpperCase()) {
            case "GOLD": return 5;
            case "SILVER": return 3;
            case "STRIVER": return 2;
            default: throw new IllegalArgumentException("Invalid membership type");
        }
    }

    private int getMembershipPrice(String membershipName) {
        switch(membershipName.toUpperCase()) {
            case "GOLD": return 50;
            case "SILVER": return 30;
            case "STRIVER": return 10;
            default: throw new IllegalArgumentException("Invalid membership type");
        }
    }

    private String generateMembershipCode(User user) {
        return "MEM" + user.getRole().toString().substring(0, 3) + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("success", "false");
        errorResponse.put("error", message);
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
    
    // Helper method to update session attributes for student dashboard
    private void updateStudentDashboard(HttpSession session, UUID userId) {
        try {
            // Get active membership
            Membership activeMembership = membershipDao.getActiveMembershipForUser(userId);
            session.setAttribute("activeMembership", activeMembership);
            
            // Get pending memberships if no active membership
            if (activeMembership == null) {
                List<Membership> pendingMemberships = membershipDao.getMembershipsByStatus(Status.PENDING);
                pendingMemberships.removeIf(m -> !m.getUser().getPersonId().equals(userId));
                session.setAttribute("pendingMemberships", !pendingMemberships.isEmpty() ? pendingMemberships : null);
                
                // Check for rejected memberships
                List<Membership> rejectedMemberships = membershipDao.getMembershipsByStatus(Status.REJECTED);
                rejectedMemberships.removeIf(m -> !m.getUser().getPersonId().equals(userId));
                session.setAttribute("rejectedMembership", !rejectedMemberships.isEmpty() ? rejectedMemberships.get(0) : null);
            } else {
                // Clear other membership states if there's an active membership
                session.removeAttribute("pendingMemberships");
                session.removeAttribute("rejectedMembership");
            }
            
            // Update borrowed books count and fines
            int borrowedBooksCount = membershipDao.getCurrentBorrowingsCount(userId);
            session.setAttribute("borrowedBooksCount", borrowedBooksCount);
            
            int unpaidFines = membershipDao.getUnpaidFines(userId);
            session.setAttribute("unpaidFines", unpaidFines);
            
            // Update membership history
            List<Membership> allMemberships = membershipDao.getMembershipsByUser(userId);
            session.setAttribute("allMemberships", allMemberships);
        } catch (Exception e) {
            System.err.println("Error updating student dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method to add status notification
    private void addStatusNotification(HttpSession session, String title, String message, String status) {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("title", title);
        statusUpdate.put("message", message);
        statusUpdate.put("status", status); 
        session.setAttribute("membershipStatusUpdate", statusUpdate);
    }
}