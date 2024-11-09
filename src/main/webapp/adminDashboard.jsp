<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auca.librarymanagement.model.*" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.isAdministrator()) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <jsp:include page="header.jsp" />

    <div class="container mx-auto p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <!-- Analytics -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-purple-600">Library Analytics</h2>
                <ul class="space-y-2">
                    <li><a href="analytics/overview.jsp" class="text-gray-700 hover:text-purple-600 block">Overview</a></li>
                    <li><a href="analytics/reports.jsp" class="text-gray-700 hover:text-purple-600 block">Reports</a></li>
                    <li><a href="analytics/statistics.jsp" class="text-gray-700 hover:text-purple-600 block">Statistics</a></li>
                </ul>
            </div>

            <!-- User Management -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-purple-600">User Management</h2>
                <ul class="space-y-2">
                    <li><a href="users/view.jsp" class="text-gray-700 hover:text-purple-600 block">View Users</a></li>
                    <li><a href="users/roles.jsp" class="text-gray-700 hover:text-purple-600 block">Role Management</a></li>
                </ul>
            </div>

            <!-- Library Resources -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-purple-600">Resources</h2>
                <ul class="space-y-2">
                    <li><a href="resources/inventory.jsp" class="text-gray-700 hover:text-purple-600 block">Book Inventory</a></li>
                    <li><a href="resources/memberships.jsp" class="text-gray-700 hover:text-purple-600 block">Memberships</a></li>
                    <li><a href="resources/transactions.jsp" class="text-gray-700 hover:text-purple-600 block">Transactions</a></li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>