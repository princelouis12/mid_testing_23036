<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Borrow Books</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/tailwindcss/2.2.19/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Available Books</h1>

        <!-- Messages Section -->
        <c:if test="${not empty error}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                ${error}
                <c:if test="${needsMembership}">
                    <a href="views/membershipApplication.jsp" class="underline ml-2">Apply for Membership</a>
                </c:if>
            </div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
                ${success}
            </div>
        </c:if>

        <!-- Search Form -->
        <div class="bg-white p-6 rounded-lg shadow mb-8">
            <form action="BookServlet" method="GET" class="flex gap-4">
                <input type="hidden" name="action" value="search">
                <input type="text" name="searchTerm" placeholder="Search by title, ISBN, or category" 
                       class="flex-1 p-2 border rounded" value="${param.searchTerm}">
                <button type="submit" class="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700">
                    Search
                </button>
            </form>
        </div>

        <!-- Active Membership Info (if exists) -->
        <c:if test="${not empty sessionScope.activeMembership}">
            <div class="bg-blue-50 border border-blue-200 p-4 rounded-lg mb-8">
                <h2 class="text-lg font-semibold mb-2">Active Membership</h2>
                <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                    <div>
                        <span class="font-medium">Type:</span> 
                        ${sessionScope.activeMembership.membershipType.membershipName}
                    </div>
                    <div>
                        <span class="font-medium">Books Allowed:</span> 
                        ${sessionScope.activeMembership.membershipType.maxBooks}
                    </div>
                    <div>
                        <span class="font-medium">Currently Borrowed:</span> 
                        ${sessionScope.activeMembership.borrowings.size()}
                    </div>
                    <div>
                        <span class="font-medium">Expires:</span> 
                        <fmt:formatDate value="${sessionScope.activeMembership.expiringTime}" pattern="dd/MM/yyyy"/>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Books List -->
        <div class="bg-white p-6 rounded-lg shadow">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <c:forEach items="${books}" var="book">
                    <div class="border p-4 rounded hover:shadow-lg transition-shadow">
                        <h3 class="text-lg font-semibold mb-2">${book.title}</h3>
                        <div class="space-y-2 text-sm">
                            <p><span class="font-medium">ISBN:</span> ${book.isbnCode}</p>
                            <p><span class="font-medium">Edition:</span> ${book.edition}</p>
                            <p><span class="font-medium">Publisher:</span> ${book.publisherName}</p>
                            <p><span class="font-medium">Category:</span> ${book.shelf.book_category}</p>
                            <p><span class="font-medium">Location:</span> Room ${book.shelf.room.room_code}</p>
                            <p><span class="font-medium">Publication Date:</span> 
                                <fmt:formatDate value="${book.publicationYear}" pattern="yyyy-MM-dd"/>
                            </p>
                        </div>
                        
                        <c:choose>
                            <c:when test="${empty sessionScope.activeMembership}">
                                <a href="views/membershipApplication.jsp" 
                                   class="block text-center mt-4 bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600">
                                    Need Membership to Borrow
                                </a>
                            </c:when>
                            <c:when test="${not sessionScope.activeMembership.canBorrowMore()}">
                                <button disabled 
                                        class="block w-full mt-4 bg-gray-400 text-white px-4 py-2 rounded cursor-not-allowed">
                                    Borrowing Limit Reached
                                </button>
                            </c:when>
                            <c:otherwise>
                                <form action="BorrowServlet" method="POST" class="mt-4">
                                    <input type="hidden" name="action" value="borrow">
                                    <input type="hidden" name="bookId" value="${book.bookId}">
                                    <button type="submit" 
                                            class="w-full bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
                                        Borrow Book
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>

                <c:if test="${empty books}">
                    <div class="col-span-full text-center py-8 text-gray-500">
                        No books available matching your search criteria.
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>