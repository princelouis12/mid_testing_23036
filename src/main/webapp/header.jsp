<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auca.librarymanagement.model.*" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("../login.jsp");
        return;
    }
    String dashboardTitle = "";
    String bgColor = "bg-indigo-600";
    
    switch(user.getRole()) {
        case LIBRARIAN:
            dashboardTitle = "Librarian Dashboard";
            break;
        case STUDENT:
            dashboardTitle = "Student Dashboard";
            bgColor = "bg-blue-600";
            break;
        case TEACHER:
            dashboardTitle = "Teacher Dashboard";
            bgColor = "bg-green-600";
            break;
        case MANAGER:
            dashboardTitle = "Manager Dashboard";
            bgColor = "bg-purple-600";
            break;
        case DEAN:
            dashboardTitle = "Dean Dashboard";
            bgColor = "bg-red-600";
            break;
        case HOD:
            dashboardTitle = "HOD Dashboard";
            bgColor = "bg-yellow-600";
            break;
    }
%>
<nav class="<%= bgColor %> text-white p-4">
    <div class="container mx-auto flex justify-between items-center">
        <h1 class="text-2xl font-bold"><%= dashboardTitle %></h1>
        <div class="flex items-center space-x-4">
            <span>Welcome, <%= user.getFirstName() + " " + user.getLastName() %></span>
            <form action="../logout" method="post">
                <button type="submit" class="bg-red-500 px-4 py-2 rounded hover:bg-red-600">Logout</button>
            </form>
        </div>
    </div>
</nav>