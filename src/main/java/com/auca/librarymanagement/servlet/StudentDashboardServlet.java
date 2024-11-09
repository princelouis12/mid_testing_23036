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

@WebServlet("/studentDashboard")
public class StudentDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final MembershipDao membershipDao = new MembershipDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User currentUser = (User) session.getAttribute("user");
            
            // Get all user's memberships with eagerly loaded collections
            List<Membership> userMemberships = membershipDao.getMembershipsByUser(currentUser.getPersonId());
            
            // Find the active membership
            Optional<Membership> activeMembership = userMemberships.stream()
                .filter(m -> m.getStatus() == Status.APPROVED && 
                           m.getExpiringTime().after(new Date()))
                .findFirst();
            
            // Set request attributes
            request.setAttribute("userMemberships", userMemberships);
            activeMembership.ifPresent(m -> request.setAttribute("activeMembership", m));
            
            // Calculate unpaid fines total
            int unpaidFines = userMemberships.stream()
                .flatMap(m -> m.getBorrowings().stream())
                .mapToInt(Borrower::getFine)
                .sum();
            request.setAttribute("unpaidFines", unpaidFines);
            
            request.getRequestDispatcher("/studentDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }
}