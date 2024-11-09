<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Library Books - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-3xl font-bold">Library Books</h1>
            
            <!-- Search and Filter Section -->
            <div class="flex space-x-4">
                <form action="student/books" method="GET" 
                      class="flex items-center space-x-2">
                    <input type="text" name="search" value="${param.search}"
                           placeholder="Search books..."
                           class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                    
                    <select name="category" class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category}" ${param.category == category ? 'selected' : ''}>
                                ${category}
                            </option>
                        </c:forEach>
                    </select>
                    
                    <select name="status" class="px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                        <option value="">All Status</option>
                        <option value="AVAILABLE" ${param.status == 'AVAILABLE' ? 'selected' : ''}>Available</option>
                        <option value="BORROWED" ${param.status == 'BORROWED' ? 'selected' : ''}>Borrowed</option>
                        <option value="RESERVED" ${param.status == 'RESERVED' ? 'selected' : ''}>Reserved</option>
                    </select>
                    
                    <button type="submit" 
                            class="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition duration-200">
                        Search
                    </button>
                </form>
            </div>
        </div>

        <!-- Membership Status Alert -->
        <c:if test="${empty sessionScope.activeMembership}">
            <div class="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 mb-6">
                <div class="flex items-center">
                    <div class="py-1">
                        <svg class="h-6 w-6 text-yellow-500 mr-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                        </svg>
                    </div>
                    <div>
                        <p class="font-bold">Membership Required</p>
                        <p>You need an active membership to borrow books. 
                           <a href="${pageContext.request.contextPath}/membership" class="underline hover:text-yellow-800">
                               Apply for membership
                           </a>
                        </p>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Books Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <c:forEach items="${books}" var="book">
                <div class="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition duration-300">
                    <div class="p-6">
                        <!-- Book Title and Status Badge -->
                        <div class="flex justify-between items-start mb-4">
                            <h2 class="text-xl font-semibold">${book.title}</h2>
                            <span class="px-2 py-1 text-sm rounded-full ${book.status == 'AVAILABLE' ? 'bg-green-100 text-green-800' : 
                                                                         book.status == 'BORROWED' ? 'bg-red-100 text-red-800' : 
                                                                         'bg-yellow-100 text-yellow-800'}">
                                ${book.status}
                            </span>
                        </div>

                        <!-- Book Details -->
                        <div class="space-y-2 text-sm text-gray-600">
                            <p>
                                <span class="font-medium">ISBN:</span> 
                                ${book.isbnCode}
                            </p>
                            <p>
                                <span class="font-medium">Edition:</span> 
                                ${book.edition}
                            </p>
                            <p>
                                <span class="font-medium">Publisher:</span> 
                                ${book.publisherName}
                            </p>
                            <p>
                                <span class="font-medium">Published:</span> 
                                <fmt:formatDate value="${book.publicationYear}" pattern="yyyy"/>
                            </p>
                            <p>
                                <span class="font-medium">Location:</span> 
                                Room ${book.shelf.room.room_code} - ${book.shelf.book_category}
                            </p>
                        </div>

                        <!-- Action Button -->
                        <div class="mt-6">
                            <c:choose>
                                <c:when test="${book.status == 'AVAILABLE' && not empty sessionScope.activeMembership}">
                                    <button onclick="borrowBook('${book.bookId}')"
                                            class="w-full bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 
                                                   transition duration-200 transform hover:-translate-y-1">
                                        Borrow Book
                                    </button>
                                </c:when>
                                <c:when test="${book.status == 'AVAILABLE' && empty sessionScope.activeMembership}">
                                    <button disabled
                                            class="w-full bg-gray-300 text-gray-500 px-4 py-2 rounded-lg cursor-not-allowed">
                                        Membership Required
                                    </button>
                                </c:when>
                                <c:when test="${book.status == 'BORROWED'}">
                                    <button disabled
                                            class="w-full bg-red-100 text-red-800 px-4 py-2 rounded-lg cursor-not-allowed">
                                        Currently Borrowed
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button disabled
                                            class="w-full bg-yellow-100 text-yellow-800 px-4 py-2 rounded-lg cursor-not-allowed">
                                        Reserved
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- No Books Found Message -->
        <c:if test="${empty books}">
            <div class="text-center py-12">
                <h3 class="text-xl text-gray-500">No books found</h3>
                <p class="text-gray-400 mt-2">Try adjusting your search or filters</p>
            </div>
        </c:if>
    </div>

    <script>
        function borrowBook(bookId) {
            Swal.fire({
                title: 'Confirm Borrowing',
                text: 'Would you like to borrow this book?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, borrow it',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch('${pageContext.request.contextPath}/student/books/borrow', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: `bookId=${bookId}`
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success === "true") {
                            Swal.fire({
                                title: 'Success!',
                                text: data.message,
                                icon: 'success'
                            }).then(() => {
                                window.location.reload();
                            });
                        } else {
                            Swal.fire({
                                title: 'Error!',
                                text: data.error,
                                icon: 'error'
                            });
                        }
                    })
                    .catch(error => {
                        Swal.fire({
                            title: 'Error!',
                            text: 'An error occurred while processing your request.',
                            icon: 'error'
                        });
                    });
                }
            });
        }
    </script>
</body>
</html>