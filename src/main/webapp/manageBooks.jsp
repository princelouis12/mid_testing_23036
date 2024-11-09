<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Books</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Manage Books</h1>
        
        <!-- Error Messages -->
        <c:if test="${not empty error}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4">
                ${error}
            </div>
        </c:if>

        <!-- Success Messages -->
        <c:if test="${not empty success}">
            <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4">
                ${success}
            </div>
        </c:if>
        
        
        <!-- Add New Book Form -->
        <div class="bg-white p-6 rounded-lg shadow mb-8">
            <h2 class="text-xl font-semibold mb-4">Add New Book</h2>
            <form action="BookServlet" method="POST" class="space-y-4">
                <input type="hidden" name="action" value="add">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium mb-1">Title</label>
                        <input type="text" name="title" required 
                               pattern="^[A-Za-z0-9\s\-,.()]{2,100}$"
                               title="Title must be between 2 and 100 characters and can contain letters, numbers, spaces, and basic punctuation"
                               class="w-full p-2 border rounded"
                               value="${param.title}">
                    </div>
                    <div>
                        <label class="block text-sm font-medium mb-1">ISBN</label>
                        <input type="text" name="isbnCode" required 
                               pattern="^(?:\d{10}|\d{13})$"
                               title="ISBN must be either 10 or 13 digits"
                               class="w-full p-2 border rounded"
                               value="${param.isbnCode}">
                    </div>
                    <div>
                        <label class="block text-sm font-medium mb-1">Edition</label>
                        <input type="number" name="edition" required 
                               min="1" max="100"
                               class="w-full p-2 border rounded"
                               value="${param.edition}">
                    </div>
                    <div>
                        <label class="block text-sm font-medium mb-1">Publisher</label>
                        <input type="text" name="publisherName" required 
                               pattern="^[A-Za-z0-9\s\-,.()]{2,50}$"
                               title="Publisher name must be between 2 and 50 characters"
                               class="w-full p-2 border rounded"
                               value="${param.publisherName}">
                    </div>
                    <div>
                        <label class="block text-sm font-medium mb-1">Publication Year</label>
                        <input type="date" name="publicationYear" required 
                               class="w-full p-2 border rounded"
                               value="${param.publicationYear}">
                    </div>
                    <div>
                        <label class="block text-sm font-medium mb-1">Shelf</label>
                        <select name="shelfId" required class="w-full p-2 border rounded">
    <option value="">Select a shelf</option>
                <c:forEach items="${shelves}" var="shelf">
                    <option value="${shelf.shelf_id}">
                        Room ${shelf.room.room_code} - ${shelf.book_category} 
                        (Available Space: ${shelf.available_stock})
                    </option>
    </c:forEach>
</select>
                    </div>
                </div>
                <button type="submit" 
                        class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 
                               transition duration-200 ease-in-out transform hover:-translate-y-1">
                    Add Book
                </button>
            </form>
        </div>
        
        <!-- Book List -->
        <div class="bg-white p-6 rounded-lg shadow">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-xl font-semibold">Book Inventory</h2>
                
                <!-- Search form -->
                <form action="BookServlet" method="GET" class="flex gap-2">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="searchTerm" 
                           placeholder="Search books..." 
                           class="p-2 border rounded"
                           value="${param.searchTerm}">
                    <button type="submit" 
                            class="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600">
                        Search
                    </button>
                </form>
            </div>

            <div class="overflow-x-auto">
                <table class="w-full">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Title</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ISBN</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Edition</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Publisher</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Location</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-gray-200">
                        <c:choose>
                            <c:when test="${empty books}">
                                <tr>
                                    <td colspan="7" class="px-6 py-4 text-center text-gray-500">
                                        Books Registered in the system are displayed here, Please click Search button!
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${books}" var="book">
                                    <tr>
                                        <td class="px-6 py-4">${book.title}</td>
                                        <td class="px-6 py-4">${book.isbnCode}</td>
                                        <td class="px-6 py-4">${book.edition}</td>
                                        <td class="px-6 py-4">${book.publisherName}</td>
                                        <td class="px-6 py-4">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                                       ${book.status eq 'AVAILABLE' ? 'bg-green-100 text-green-800' : 
                                                         book.status eq 'BORROWED' ? 'bg-yellow-100 text-yellow-800' : 
                                                         'bg-red-100 text-red-800'}">
                                                ${book.status}
                                            </span>
                                        </td>
                                        <td class="px-6 py-4">${book.shelf.room.room_code} - ${book.shelf.book_category}</td>
                                        <td class="px-6 py-4 space-x-2">
                                            <a href="BookServlet?action=edit&id=${book.bookId}" 
                                               class="text-blue-600 hover:text-blue-900 hover:underline">
                                                Edit
                                            </a>
                                            <c:if test="${book.status eq 'AVAILABLE'}">
                                                <a href="BookServlet?action=delete&id=${book.bookId}" 
                                                   class="text-red-600 hover:text-red-900 hover:underline"
                                                   onclick="return confirm('Are you sure you want to delete this book?')">
                                                    Delete
                                                </a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Optional: Add JavaScript for form validation -->
    <script>
        // Prevent form submission if no shelf is selected
        document.querySelector('form').addEventListener('submit', function(e) {
            const shelfSelect = document.querySelector('select[name="shelfId"]');
            if (!shelfSelect.value) {
                e.preventDefault();
                alert('Please select a shelf');
            }
        });
    </script>
</body>
</html>