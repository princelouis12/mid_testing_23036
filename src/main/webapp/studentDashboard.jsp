<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <!-- Navigation -->
    <nav class="bg-blue-600 text-white shadow-lg">
        <div class="container mx-auto px-6 py-4">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-8">
                    <div class="text-xl font-bold">Library Management System</div>
                    <div class="hidden md:flex space-x-4">
                        <a href="${pageContext.request.contextPath}/studentDashboard" 
                           class="hover:text-gray-200 px-3 py-2 rounded-md hover:bg-blue-700">
                            Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/student/books" 
                           class="hover:text-gray-200 px-3 py-2 rounded-md hover:bg-blue-700">
                            Browse Books
                        </a>
                        <a href="${pageContext.request.contextPath}/student/books" 
                           class="hover:text-gray-200 px-3 py-2 rounded-md hover:bg-blue-700">
                            Available Books
                        </a>
                    </div>
                </div>
                <div class="flex items-center space-x-4">
                    <span>Welcome, ${sessionScope.user.firstName}</span>
                    <a href="${pageContext.request.contextPath}/logout" 
                       class="hover:text-gray-200 px-3 py-2 rounded-md hover:bg-blue-700">
                        Logout
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mx-auto px-6 py-8">
        <h1 class="text-3xl font-bold mb-8">Student Dashboard</h1>

        <!-- Membership Status Section -->
        <div class="bg-white rounded-lg shadow p-6 mb-8">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-xl font-semibold">Membership Status</h2>
                <c:if test="${empty activeMembership && empty pendingMemberships}">
                    <a href="applyMembership.jsp" 
                       class="inline-block bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition duration-300">
                        Apply for New Membership
                    </a>
                </c:if>
            </div>

            <div class="space-y-6">
                <!-- Active Membership Display -->
                <c:choose>
                    <c:when test="${not empty activeMembership}">
                        <div class="bg-green-50 border border-green-200 rounded-lg p-6">
                            <div class="flex items-center justify-between mb-4">
                                <h3 class="text-lg font-semibold text-green-700">Active Membership</h3>
                                <span class="px-3 py-1 rounded-full bg-green-100 text-green-800 text-sm font-medium">
                                    Active
                                </span>
                            </div>
                            
                            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                                <div>
                                    <p class="text-gray-600 text-sm">Membership Type</p>
                                    <p class="font-semibold text-lg flex items-center">
                                        ${activeMembership.membershipType.membershipName}
                                        <c:if test="${activeMembership.membershipType.membershipName eq 'GOLD'}">
                                            <span class="ml-1 text-yellow-500">★</span>
                                        </c:if>
                                    </p>
                                </div>
                                
                                <div>
                                    <p class="text-gray-600 text-sm">Books Allowance</p>
                                    <p class="font-semibold text-lg">
                                        ${activeMembership.membershipType.maxBooks} books
                                    </p>
                                </div>
                                
                                <div>
                                    <p class="text-gray-600 text-sm">Daily Rate</p>
                                    <p class="font-semibold text-lg">
                                        ${activeMembership.membershipType.price} Rwf/day
                                    </p>
                                </div>
                                
                                <div>
                                    <p class="text-gray-600 text-sm">Membership Code</p>
                                    <p class="font-medium">${activeMembership.membershipCode}</p>
                                </div>
                                
                                <div>
                                    <p class="text-gray-600 text-sm">Start Date</p>
                                    <p class="font-medium">
                                        <fmt:formatDate value="${activeMembership.registrationDate}" pattern="MMM dd, yyyy"/>
                                    </p>
                                </div>
                                
                                <div>
                                    <p class="text-gray-600 text-sm">Expiry Date</p>
                                    <p class="font-medium">
                                        <fmt:formatDate value="${activeMembership.expiringTime}" pattern="MMM dd, yyyy"/>
                                    </p>
                                </div>
                            </div>
                            
                            <div class="mt-6 p-4 bg-blue-50 rounded-lg">
                                <div class="flex items-center">
                                    <div class="flex-shrink-0">
                                        <svg class="h-5 w-5 text-blue-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
                                        </svg>
                                    </div>
                                    <div class="ml-3">
                                        <h4 class="text-sm font-medium text-blue-800">Currently Available</h4>
                                        <p class="mt-1 text-sm text-blue-700">
                                            You can borrow up to ${activeMembership.membershipType.maxBooks - borrowedBooksCount} more books
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Pending Memberships -->
                        <c:choose>
                            <c:when test="${not empty pendingMemberships}">
                                <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
                                    <div class="flex items-center justify-between mb-4">
                                        <h3 class="text-lg font-semibold text-yellow-700">Pending Membership Application</h3>
                                        <span class="px-3 py-1 rounded-full bg-yellow-100 text-yellow-800 text-sm font-medium">
                                            Pending Approval
                                        </span>
                                    </div>
                                    <c:forEach items="${pendingMemberships}" var="membership">
                                        <div class="mt-3">
                                            <p class="text-gray-700">
                                                Your ${membership.membershipType.membershipName} membership application is under review.
                                            </p>
                                            <p class="text-sm text-gray-600 mt-2">
                                                Application Date: <fmt:formatDate value="${membership.registrationDate}" pattern="MMM dd, yyyy"/>
                                            </p>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-8 bg-gray-50 rounded-lg border border-gray-200">
                                    <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 48 48">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M34 40h10v-4a6 6 0 00-10.712-3.714M34 40H14m20 0v-4a9.971 9.971 0 00-.712-3.714M14 40H4v-4a6 6 0 0110.713-3.714M14 40v-4c0-1.313.253-2.566.713-3.714m0 0A10.003 10.003 0 0124 26c4.21 0 7.813 2.602 9.288 6.286"/>
                                    </svg>
                                    <h3 class="mt-2 text-sm font-medium text-gray-900">No Active Membership</h3>
                                    <p class="mt-1 text-sm text-gray-500">Get started by applying for a membership.</p>
                                    <div class="mt-6">
                                        <a href="applyMembership.jsp" 
                                           class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 transition duration-300">
                                            Apply Now
                                        </a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>

                <!-- Membership History -->
                <c:if test="${not empty allMemberships}">
                    <div class="mt-8">
                        <h3 class="text-lg font-semibold mb-4">Membership History</h3>
                        <div class="space-y-4">
                            <c:forEach items="${allMemberships}" var="membership">
                                <div class="border rounded-lg p-4 
                                    ${membership.status eq 'APPROVED' ? 'border-green-200 bg-green-50' : 
                                      membership.status eq 'REJECTED' ? 'border-red-200 bg-red-50' : 
                                      'border-yellow-200 bg-yellow-50'}">
                                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <div>
                                            <p class="text-gray-600 text-sm">Membership Type</p>
                                            <p class="font-medium">${membership.membershipType.membershipName}</p>
                                        </div>
                                        <div>
                                            <p class="text-gray-600 text-sm">Status</p>
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full 
                                                ${membership.status eq 'APPROVED' ? 'bg-green-100 text-green-800' : 
                                                  membership.status eq 'REJECTED' ? 'bg-red-100 text-red-800' : 
                                                  'bg-yellow-100 text-yellow-800'}">
                                                ${membership.status}
                                            </span>
                                        </div>
                                        <div>
                                            <p class="text-gray-600 text-sm">Application Date</p>
                                            <p class="text-sm">
                                                <fmt:formatDate value="${membership.registrationDate}" pattern="MMM dd, yyyy"/>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition duration-200">
                <h3 class="text-lg font-semibold mb-3">Browse Library</h3>
                <p class="text-gray-600 mb-4">Browse our complete collection of books.</p>
                <a href="bookList.jsp" 
                   class="block w-full text-center bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition duration-300">
                    View All Books
                </a>
            </div>

            <div class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition duration-200">
                <h3 class="text-lg font-semibold mb-3">Available Books</h3>
                <p class="text-gray-600 mb-4">See which books are ready to borrow.</p>
                <a href="availableBooks.jsp" 
                   class="block w-full text-center bg-green-500 text-white py-2 rounded hover:bg-green-600 transition duration-300">
                    View Available Books
                </a>
            </div>

            <div class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition duration-200">
                <h3 class="text-lg font-semibold mb-3">My Borrowings</h3>
                <p class="text-gray-600 mb-4">Track your borrowed books and returns.</p>
                <a href="${pageContext.request.contextPath}/student/borrowings" 
                   class="block w-full text-center bg-indigo-500 text-white py-2 rounded hover:bg-indigo-600 transition duration-300">
                    View My Books
                </a>
            </div>
        </div>

        <!-- Account Information -->
        <div class="bg-white rounded-lg shadow p-6">
            <h2 class="text-xl font-semibold mb-4">Account Information</h2>
            
            <!-- Fines Alert -->
            <c:if test="${unpaidFines > 0}">
                <div class="mb-6 bg-red-50 border-l-4 border-red-500 p-4">
                    <div class="flex">
                        <div class="flex-shrink-0">
                            <svg class="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                            </svg>
                        </div>
                        <div class="ml-3">
                            <h3 class="text-sm font-medium text-red-800">Outstanding Fines</h3>
                            <div class="mt-2 text-sm text-red-700">
                                <p>You have unpaid fines totaling ${unpaidFines} Rwf.</p>
                                <p class="mt-1">Please clear your fines to continue borrowing books.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- User Details -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-4">
                    <div>
                        <h3 class="text-sm font-medium text-gray-500">Personal Information</h3>
                        <div class="mt-2 border rounded-lg p-4">
                            <p class="text-sm text-gray-600">Name</p>
                            <p class="font-medium">${sessionScope.user.firstName} ${sessionScope.user.lastName}</p>
                            
                            <p class="text-sm text-gray-600 mt-3">Student ID</p>
                            <p class="font-medium">${sessionScope.user.userName}</p>
                            
                            <p class="text-sm text-gray-600 mt-3">Phone Number</p>
                            <p class="font-medium">${sessionScope.user.phoneNumber}</p>
                        </div>
                    </div>
                </div>

                <div class="space-y-4">
                    <div>
                        <h3 class="text-sm font-medium text-gray-500">Library Activity Summary</h3>
                        <div class="mt-2 border rounded-lg p-4">
                            <div class="flex justify-between items-center mb-3">
                                <p class="text-sm text-gray-600">Current Borrowings</p>
                                <span class="font-medium">${borrowedBooksCount}</span>
                            </div>
                            
                            <div class="flex justify-between items-center mb-3">
                                <p class="text-sm text-gray-600">Books Available to Borrow</p>
                                <span class="font-medium">
                                    <c:choose>
                                        <c:when test="${not empty activeMembership}">
                                            ${activeMembership.membershipType.maxBooks - borrowedBooksCount}
                                        </c:when>
                                        <c:otherwise>
                                            0
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>

                            <div class="flex justify-between items-center">
                                <p class="text-sm text-gray-600">Account Status</p>
                                <span class="px-2 py-1 text-xs font-semibold rounded-full
                                    ${unpaidFines > 0 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}">
                                    ${unpaidFines > 0 ? 'Has Unpaid Fines' : 'Good Standing'}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Recent Activity -->
            <div class="mt-6">
                <h3 class="text-sm font-medium text-gray-500 mb-4">Recent Activity</h3>
                <c:choose>
                    <c:when test="${not empty recentActivity}">
                        <div class="border rounded-lg divide-y">
                            <c:forEach items="${recentActivity}" var="activity">
                                <div class="p-4">
                                    <div class="flex items-center justify-between">
                                        <div>
                                            <p class="text-sm font-medium text-gray-900">${activity.description}</p>
                                            <p class="text-sm text-gray-500">${activity.bookTitle}</p>
                                        </div>
                                        <p class="text-sm text-gray-500">
                                            <fmt:formatDate value="${activity.date}" pattern="MMM dd, yyyy"/>
                                        </p>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-6 bg-gray-50 rounded-lg border border-gray-200">
                            <p class="text-gray-500">No recent activity to display</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Notifications for membership status changes -->
    <c:if test="${not empty membershipStatusUpdate}">
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                Swal.fire({
                    title: '${membershipStatusUpdate.title}',
                    text: '${membershipStatusUpdate.message}',
                    icon: '${membershipStatusUpdate.status}',
                    confirmButtonText: 'OK'
                });
            });
        </script>
    </c:if>
</body>
</html>