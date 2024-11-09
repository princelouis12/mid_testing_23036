package com.auca.librarymanagement.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.auca.librarymanagement.model.User;
import com.auca.librarymanagement.model.RoleType;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String path = httpRequest.getRequestURI();
        
        // Allow access to login and register pages
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }
        
        // Check role-based access
        User user = (User) session.getAttribute("user");
        if (!hasAccess(user, path)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }
        
        chain.doFilter(request, response);
    }

    private boolean isPublicResource(String path) {
        return path.endsWith("login.jsp") || 
               path.endsWith("register.jsp") ||
               path.endsWith("/login") ||
               path.endsWith("/register") ||
               path.contains("/assets/") ||
               path.contains("/css/") ||
               path.contains("/js/");
    }

    private boolean hasAccess(User user, String path) {
        if (path.contains("/admin/") && !user.isAdministrator()) {
            return false;
        }
        if (path.contains("/librarian/") && !user.isLibrarian()) {
            return false;
        }
        if (path.contains("/student/") && user.getRole() != RoleType.STUDENT) {
            return false;
        }
        if (path.contains("/teacher/") && user.getRole() != RoleType.TEACHER) {
            return false;
        }
        return true;
    }

    @Override
    public void destroy() {}
}