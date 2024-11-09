<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Borrowings - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Manage Borrowings</h1>
        
        <!-- Overdue Books Section -->
        <div class="bg-white rounded-lg shadow-lg p-6 mb-8">
            <h2 class="text-xl font-semibold mb-4 text-red-600">Overdue Books</h2>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Book</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Borrower</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Due Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Days Overdue</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fine</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach items="${overdueBorrowings}" var="borrowing">
                            <tr class="bg-red-50">
                                <td class="px-6 py-4">
                                    ${borrowing.book.title}
                                    <div class="text-sm text-gray-500">ISBN: ${borrowing.book.isbn_code}</div>
                                </td>
                                <td class="px-6 py-4">
                                    ${borrowing.reader.first_name} ${borrowing.reader.last_name}
                                    <div class="text-sm text-gray-500">${borrowing.membership.membership_code}</div>
                                </td>
                                <td class="px-6 py-4">
                                    <fmt:formatDate value="${borrowing.due_date}" pattern="MMM dd, yyyy"/>
                                </td>
                                <td class="px-6 py-4 text-red-600 font-semibold">
                                    ${borrowing.daysOverdue}
                                </td>
                                <td class="px-6 py-4">
                                    ${borrowing.fine} Rwf
                                </td>
                                <td class="px-6 py-4">
                                    <button onclick="processReturn('${borrowing.id}')"
                                            class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600">
                                        Process Return
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- Active Borrowings Section -->
        <div class="bg-white rounded-lg shadow-lg p-6">
            <div class="flex justify-between items-center mb-4">
                <h2 class="text-xl font-semibold">Active Borrowings</h2>
                <button onclick="showNewBorrowingForm()" 
                        class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
                    New Borrowing
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Book</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Borrower</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Pickup Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Due Date</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Membership</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <c:forEach items="${borrowings}" var="borrowing">
                            <c:if test="${borrowing.return_date == null}">
                                <tr>
                                    <td class="px-6 py-4">
                                        ${borrowing.book.title}
                                        <div class="text-sm text-gray-500">ISBN: ${borrowing.book.isbn_code}</div>
                                    </td>
                                    <td class="px-6 py-4">
                                        ${borrowing.reader.first_name} ${borrowing.reader.last_name}
                                    </td>
                                    <td class="px-6 py-4">
                                        <fmt:formatDate value="${borrowing.pickup_date}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td class="px-6 py-4">
                                        <fmt:formatDate value="${borrowing.due_date}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td class="px-6 py-4">
                                        ${borrowing.membership.membershipType.membership_name}
                                    </td>
                                    <td class="px-6 py-4">
                                        <button onclick="processReturn('${borrowing.id}')"
                                                class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600">
                                            Process Return
                                        </button>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        function showNewBorrowingForm() {
            Swal.fire({
                title: 'New Borrowing',
                html: `
                    <form id="borrowingForm" class="space-y-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700">User ID/Username</label>
                            <input type="text" id="userId" class="mt-1 block w-full border rounded-md shadow-sm">
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Book ISBN</label>
                            <input type="text" id="bookIsbn" class="mt-1 block w-full border rounded-md shadow-sm">
                        </div>
                    </form>
                `,
                showCancelButton: true,
                confirmButtonText: 'Create Borrowing',
                preConfirm: () => {
                    return {
                        userId: document.getElementById('userId').value,
                        bookIsbn: document.getElementById('bookIsbn').value
                    };
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    createBorrowing(result.value);
                }
            });
        }

        function createBorrowing(data) {
            fetch('borrow', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `userId=${data.userId}&bookIsbn=${data.bookIsbn}`
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

        function processReturn(borrowingId) {
            Swal.fire({
                title: 'Process Return',
                text: 'Are you sure you want to process this return?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonText: 'Yes, process return'
            }).then((result) => {
                if (result.isConfirmed) {
                    returnBook(borrowingId);
                }
            });
        }

        function returnBook(borrowingId) {
            fetch('borrow/return', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `borrowId=${borrowingId}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success === "true") {
                    let message = data.message;
                    if (data.fine > 0) {
                        message += `\nLate return fine: ${data.fine} Rwf`;
                    }
                    Swal.fire({
                        title: 'Success!',
                        text: message,
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
    </script>
</body>
</html>