<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Borrowings - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">My Borrowings</h1>
        
        <!-- Active Borrowings Section -->
        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-xl font-semibold">Currently Borrowed Books</h2>
                <c:if test="${activeMembership != null && activeMembership.canBorrowMore()}">
                    <button onclick="showAvailableBooks()" 
                            class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                        Borrow New Book
                    </button>
                </c:if>
            </div>
            
            <div class="mb-4">
                <c:choose>
                    <c:when test="${activeMembership == null}">
                        <div class="bg-yellow-50 border-l-4 border-yellow-400 p-4">
                            <div class="flex">
                                <div class="flex-shrink-0">
                                    <svg class="h-5 w-5 text-yellow-400" fill="currentColor" viewBox="0 0 20 20">
                                        <path d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"/>
                                    </svg>
                                </div>
                                <div class="ml-3">
                                    <p class="text-sm text-yellow-700">
                                        You need an active membership to borrow books. 
                                        <a href="membership" class="font-medium underline text-yellow-700 hover:text-yellow-600">
                                            Get membership
                                        </a>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="bg-blue-50 border-l-4 border-blue-400 p-4">
                            <p class="text-sm text-blue-700">
                                Membership Status: ${activeMembership.membershipType.membership_name} |
                                Books Available: ${activeMembership.membershipType.max_books - activeBorrowings.size()} of 
                                ${activeMembership.membershipType.max_books}
                            </p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Book Title</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Borrowed Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Due Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fine (Rwf)</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach items="${activeBorrowings}" var="borrowing">
                            <tr class="${borrowing.isOverdue() ? 'bg-red-50' : ''}">
                                <td class="px-6 py-4">
                                    ${borrowing.book.title}
                                    <div class="text-sm text-gray-500">
                                        ISBN: ${borrowing.book.isbn_code}
                                    </div>
                                </td>
                                <td class="px-6 py-4">
                                    <fmt:formatDate value="${borrowing.pickup_date}" pattern="MMM dd, yyyy"/>
                                </td>
                                <td class="px-6 py-4">
                                    <fmt:formatDate value="${borrowing.due_date}" pattern="MMM dd, yyyy"/>
                                </td>
                                <td class="px-6 py-4">
                                    <c:choose>
                                        <c:when test="${borrowing.isOverdue()}">
                                            <span class="px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">
                                                Overdue
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                                                Active
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="px-6 py-4">
                                    <c:if test="${borrowing.isOverdue()}">
                                        <span class="text-red-600 font-semibold">
                                            ${borrowing.calculateLateFees()}
                                        </span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Borrowing History Section -->
        <div class="bg-white rounded-lg shadow-lg p-6">
            <h2 class="text-xl font-semibold mb-4">Borrowing History</h2>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Book Title</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Borrowed Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Return Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Membership Type</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fine Paid (Rwf)</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach items="${borrowingHistory}" var="borrowing">
                            <tr>
                                <td class="px-6 py-4">
                                    ${borrowing.book.title}
                                    <div class="text-sm text-gray-500">
                                        ISBN: ${borrowing.book.isbn_code}
                                    </div>
                                </td>
                                <td class="px-6 py-4">
                                    <fmt:formatDate value="${borrowing.pickup_date}" pattern="MMM dd, yyyy"/>
                                </td>
                                <td class="px-6 py-4">
                                    <fmt:formatDate value="${borrowing.return_date}" pattern="MMM dd, yyyy"/>
                                </td>
                                <td class="px-6 py-4">
                                    ${borrowing.membership.membershipType.membership_name}
                                </td>
                                <td class="px-6 py-4">
                                    ${borrowing.fine > 0 ? borrowing.fine : '-'}
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        function showAvailableBooks() {
            fetch('book/available')
                .then(response => response.json())
                .then(books => {
                    const bookOptions = books.map(book => `
                        <div onclick="borrowBook('${book.book_id}')" 
                             class="cursor-pointer p-4 border rounded-lg hover:bg-blue-50 mb-2">
                            <h3 class="font-bold">${book.title}</h3>
                            <p class="text-sm text-gray-600">ISBN: ${book.isbn_code}</p>
                            <p class="text-sm text-gray-600">Edition: ${book.edition}</p>
                            <p class="text-sm text-gray-600">Publisher: ${book.publisher_name}</p>
                        </div>
                    `).join('');

                    Swal.fire({
                        title: 'Available Books',
                        html: `<div class="max-h-96 overflow-y-auto">${bookOptions}</div>`,
                        showConfirmButton: false,
                        showCloseButton: true,
                        width: '600px'
                    });
                })
                .catch(error => {
                    Swal.fire({
                        title: 'Error!',
                        text: 'Failed to load available books.',
                        icon: 'error'
                    });
                });
        }

        function borrowBook(bookId) {
            Swal.fire({
                title: 'Confirm Borrowing',
                text: 'Would you like to borrow this book?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, borrow',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch('borrow', {
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