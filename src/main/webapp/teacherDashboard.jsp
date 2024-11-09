<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.auca.librarymanagement.model.*" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || user.getRole() != RoleType.TEACHER) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Teacher Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <jsp:include page="header.jsp" />

    <div class="container mx-auto p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <!-- My Books -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-green-600">My Books</h2>
                <ul class="space-y-2">
                    <li><a href="books/borrowed.jsp" class="text-gray-700 hover:text-green-600 block">Currently Borrowed</a></li>
                    <li><a href="books/history.jsp" class="text-gray-700 hover:text-green-600 block">Borrowing History</a></li>
                </ul>
            </div>

            <!-- Library Catalog -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-green-600">Library Catalog</h2>
                <ul class="space-y-2">
                    <li><a href="catalog/search.jsp" class="text-gray-700 hover:text-green-600 block">Search Books</a></li>
                    <li><a href="catalog/advanced.jsp" class="text-gray-700 hover:text-green-600 block">Advanced Search</a></li>
                    <li><a href="catalog/recommend.jsp" class="text-gray-700 hover:text-green-600 block">Recommend Books</a></li>
                </ul>
            </div>

            <!-- Academic Resources -->
            <div class="bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow">
                <h2 class="text-xl font-bold mb-4 text-green-600">Academic Resources</h2>
                <ul class="space-y-2">
                    <li><a href="resources/journals.jsp" class="text-gray-700 hover:text-green-600 block">Academic Journals</a></li>
                    <li><a href="resources/publications.jsp" class="text-gray-700 hover:text-green-600 block">Recent Publications</a></li>
                </ul>
            </div>
        </div>
    </div>
</body>
</html>