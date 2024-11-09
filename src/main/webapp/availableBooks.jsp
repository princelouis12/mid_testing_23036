<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Available Books - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Available Books</h1>
        
        <!-- Membership Status Check -->
        <c:if test="${empty sessionScope.activeMembership}">
            <div class="bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4 mb-6">
                <p class="font-bold">Membership Required</p>
                <p>You need an active membership to borrow books. 
                   <a href="applyMembership.jsp" 
                      class="underline hover:text-yellow-800">
                       Apply for membership
                   </a>
                </p>
            </div>
        </c:if>
        
        <!-- Books Grid -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <c:forEach items="${books}" var="book">
                <div class="bg-white rounded-lg shadow-lg overflow-hidden">
                    <div class="p-6">
                        <h2 class="text-xl font-semibold mb-2">${book.title}</h2>
                        <div class="text-sm text-gray-600 mb-4">
                            <p>ISBN: ${book.isbnCode}</p>
                            <p>Edition: ${book.edition}</p>
                            <p>Publisher: ${book.publisherName}</p>
                            <p>Location: Room ${book.shelf.room.room_code} - ${book.shelf.book_category}</p>
                        </div>
                        
                        <c:choose>
                            <c:when test="${not empty sessionScope.activeMembership}">
                                <button onclick="borrowBook('${book.bookId}')"
                                        class="w-full bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 
                                               transition duration-200 ease-in-out transform hover:-translate-y-1">
                                    Borrow Book
                                </button>
                            </c:when>
                            <c:otherwise>
                                <button disabled
                                        class="w-full bg-gray-300 text-gray-500 px-4 py-2 rounded cursor-not-allowed">
                                    Membership Required
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <script>
        function borrowBook(bookId) {
            Swal.fire({
                title: 'Confirm Borrow',
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