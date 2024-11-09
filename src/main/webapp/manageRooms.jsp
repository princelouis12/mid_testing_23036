<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Rooms</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Room Management</h1>
        
        <!-- Add New Room Form -->
        <div class="bg-white p-6 rounded-lg shadow mb-8">
            <h2 class="text-xl font-semibold mb-4">Add New Room</h2>
            <form action="RoomServlet" method="POST" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium mb-1">Room Code</label>
                    <input type="text" name="roomCode" required class="w-full p-2 border rounded">
                </div>
                <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">Add Room</button>
            </form>
        </div>
        
        <!-- Room List -->
        <div class="bg-white p-6 rounded-lg shadow mb-8">
            <h2 class="text-xl font-semibold mb-4">Room Inventory</h2>
            <div class="space-y-6">
                <c:forEach items="${rooms}" var="room">
                    <div class="border p-4 rounded">
                        <div class="flex justify-between items-center mb-4">
                            <h3 class="text-lg font-semibold">Room: ${room.room_code}</h3>
                            <div class="text-sm">
                                <span class="mr-4">Total Books: ${room.totalBooks}</span>
                                <span class="mr-4">Available: ${room.availableBooks}</span>
                                <span>Borrowed: ${room.borrowedBooks}</span>
                            </div>
                        </div>
                        
                        <!-- Add Shelf Form -->
                        <div class="mb-4 p-4 bg-gray-50 rounded">
                            <h4 class="font-medium mb-2">Add New Shelf</h4>
                            <form action="ShelfServlet" method="POST" class="flex gap-4">
                                <input type="hidden" name="roomId" value="${room.room_id}">
                                <div class="flex-1">
                                    <input type="text" name="bookCategory" placeholder="Book Category" 
                                           required class="w-full p-2 border rounded">
                                </div>
                                <div class="flex-1">
                                    <input type="number" name="initialStock" placeholder="Initial Stock" 
                                           required class="w-full p-2 border rounded">
                                </div>
                                <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
                                    Add Shelf
                                </button>
                            </form>
                        </div>
                        
                        <!-- Shelf List -->
                        <table class="w-full">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th class="px-4 py-2 text-left">Category</th>
                                    <th class="px-4 py-2 text-left">Available</th>
                                    <th class="px-4 py-2 text-left">Borrowed</th>
                                    <th class="px-4 py-2 text-left">Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${room.shelves}" var="shelf">
                                    <tr>
                                        <td class="px-4 py-2">${shelf.book_category}</td>
                                        <td class="px-4 py-2">${shelf.available_stock}</td>
                                        <td class="px-4 py-2">${shelf.borrowed_number}</td>
                                        <td class="px-4 py-2">${shelf.initial_stock}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</body>
</html>