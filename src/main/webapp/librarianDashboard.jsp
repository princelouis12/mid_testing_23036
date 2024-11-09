<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Librarian Dashboard - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <!-- Navigation -->
    <nav class="bg-blue-600 text-white shadow-lg">
        <div class="container mx-auto px-6 py-4">
            <div class="flex items-center justify-between">
                <div class="text-xl font-bold">Library Management System</div>
                <div class="flex items-center space-x-4">
                    <span>Welcome, ${sessionScope.user.firstName}</span>
                    <a href="logout" class="hover:text-gray-200">Logout</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mx-auto px-6 py-8">
        <h1 class="text-3xl font-bold mb-8">Librarian Dashboard</h1>

        <!-- Quick Stats -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <div class="bg-white rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-700">Total Books</h3>
                <p class="text-3xl font-bold text-blue-600">${totalBooks}</p>
            </div>
            <div class="bg-white rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-700">Books Borrowed</h3>
                <p class="text-3xl font-bold text-green-600">${borrowedBooks}</p>
            </div>
            <div class="bg-white rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-700">Overdue Books</h3>
                <p class="text-3xl font-bold text-red-600">${overdueBooks}</p>
            </div>
            <div class="bg-white rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-700">Pending Memberships</h3>
                <p class="text-3xl font-bold text-yellow-600">${pendingMemberships}</p>
            </div>
        </div>

        <!-- Main Features -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <!-- Book Management -->
            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-xl font-semibold mb-4">Book Management</h2>
                <div class="space-y-4">
                    <a href="manageBooks.jsp" 
                       class="block w-full text-center bg-blue-500 text-white py-2 rounded hover:bg-blue-600">
                        Manage Books
                    </a>
                    <a href="manageRooms.jsp"
                       class="block w-full text-center bg-blue-500 text-white py-2 rounded hover:bg-blue-600">
                        Manage Rooms & Shelves
                    </a>
                </div>
            </div>

            <!-- Membership Management -->
            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-xl font-semibold mb-4">Membership Management</h2>
                <div class="space-y-4">
                    <a href="#" 
                       class="block w-full text-center bg-green-500 text-white py-2 rounded hover:bg-green-600">
                        Review Memberships
                    </a>
                    <a href="membership?status=pending"
                       class="block w-full text-center bg-yellow-500 text-white py-2 rounded hover:bg-yellow-600">
                        Pending Approvals <span class="bg-white text-yellow-500 px-2 rounded-full">${pendingMemberships}</span>
                    </a>
                </div>
            </div>

            <!-- Borrowing Management -->
            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-xl font-semibold mb-4">Borrowing Management</h2>
                <div class="space-y-4">
                    <a href="borrow" 
                       class="block w-full text-center bg-indigo-500 text-white py-2 rounded hover:bg-indigo-600">
                        Manage Borrowings
                    </a>
                    <a href="borrow?filter=overdue"
                       class="block w-full text-center bg-red-500 text-white py-2 rounded hover:bg-red-600">
                        Overdue Books <span class="bg-white text-red-500 px-2 rounded-full">${overdueBooks}</span>
                    </a>
                </div>
            </div>

            <!-- Location Management -->
            <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-xl font-semibold mb-4">Location Management</h2>
                <div class="flex items-center space-x-2">
                    <svg class="w-6 h-6 text-blue-500" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zM7 9c0-2.76 2.24-5 5-5s5 2.24 5 5c0 1.98-2.33 6.33-5 9.88C9.33 15.33 7 10.98 7 9zm5-3c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
                    </svg>
                    <a href="locationManagement.jsp" class="text-blue-500 font-semibold hover:text-blue-600">
                        Manage Locations
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
