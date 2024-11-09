<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Apply for Membership - Library Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body class="bg-gray-100">
    <!-- Navigation -->
    <nav class="bg-blue-600 text-white shadow-lg">
        <div class="container mx-auto px-6 py-4">
            <div class="flex items-center justify-between">
                <div class="text-xl font-bold">Library Management System</div>
                <div class="flex items-center space-x-4">
                    <span>Welcome, ${sessionScope.user.firstName}</span>
                    <a href="${pageContext.request.contextPath}/logout" class="hover:text-gray-200">Logout</a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mx-auto px-4 py-8">
        <h1 class="text-3xl font-bold mb-8">Select Your Membership Plan</h1>

        <!-- Membership Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <!-- Gold Membership -->
            <div class="bg-white rounded-lg shadow-lg overflow-hidden transform transition duration-500 hover:scale-105">
                <div class="p-6">
                    <div class="text-center">
                        <h3 class="text-2xl font-bold text-yellow-600">Gold Membership</h3>
                        <div class="mt-4">
                            <p class="text-4xl font-bold">50 Rwf</p>
                            <p class="text-gray-600">per day</p>
                        </div>
                    </div>
                    <div class="mt-6 space-y-4">
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Borrow up to 5 books</span>
                        </div>
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Premium book access</span>
                        </div>
                    </div>
                    <button onclick="applyForMembership({
                            name: 'GOLD',
                            maxBooks: 5,
                            price: 50,
                            color: 'yellow'
                        })" 
                        class="w-full mt-6 bg-yellow-500 hover:bg-yellow-600 text-white py-2 rounded-lg transition duration-300 transform hover:-translate-y-1">
                        Apply Now
                    </button>
                </div>
            </div>

            <!-- Silver Membership -->
            <div class="bg-white rounded-lg shadow-lg overflow-hidden transform transition duration-500 hover:scale-105">
                <div class="p-6">
                    <div class="text-center">
                        <h3 class="text-2xl font-bold text-gray-600">Silver Membership</h3>
                        <div class="mt-4">
                            <p class="text-4xl font-bold">30 Rwf</p>
                            <p class="text-gray-600">per day</p>
                        </div>
                    </div>
                    <div class="mt-6 space-y-4">
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Borrow up to 3 books</span>
                        </div>
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Standard book access</span>
                        </div>
                    </div>
                    <button onclick="applyForMembership({
                            name: 'SILVER',
                            maxBooks: 3,
                            price: 30,
                            color: 'gray'
                        })" 
                        class="w-full mt-6 bg-gray-500 hover:bg-gray-600 text-white py-2 rounded-lg transition duration-300 transform hover:-translate-y-1">
                        Apply Now
                    </button>
                </div>
            </div>

            <!-- Striver Membership -->
            <div class="bg-white rounded-lg shadow-lg overflow-hidden transform transition duration-500 hover:scale-105">
                <div class="p-6">
                    <div class="text-center">
                        <h3 class="text-2xl font-bold text-blue-600">Striver Membership</h3>
                        <div class="mt-4">
                            <p class="text-4xl font-bold">10 Rwf</p>
                            <p class="text-gray-600">per day</p>
                        </div>
                    </div>
                    <div class="mt-6 space-y-4">
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Borrow up to 2 books</span>
                        </div>
                        <div class="flex items-center">
                            <svg class="h-5 w-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
                            </svg>
                            <span class="ml-2">Basic book access</span>
                        </div>
                    </div>
                    <button onclick="applyForMembership({
                            name: 'STRIVER',
                            maxBooks: 2,
                            price: 10,
                            color: 'blue'
                        })" 
                        class="w-full mt-6 bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg transition duration-300 transform hover:-translate-y-1">
                        Apply Now
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script>
    function applyForMembership(membershipDetails) {
        Swal.fire({
            title: `${membershipDetails.name} Membership Details`,
            html: `
                <form id="membershipForm" class="text-left">
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Membership Type</label>
                        <input type="text" class="w-full px-3 py-2 border rounded-lg" value="${membershipDetails.name}" readonly>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Maximum Books</label>
                        <input type="number" class="w-full px-3 py-2 border rounded-lg" value="${membershipDetails.maxBooks}" readonly>
                    </div>
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Price per Day (Rwf)</label>
                        <input type="number" class="w-full px-3 py-2 border rounded-lg" value="${membershipDetails.price}" readonly>
                    </div>
                    <p class="mt-4">Are you sure you want to apply for this membership?</p>
                </form>
            `,
            showCancelButton: true,
            confirmButtonText: 'Yes, apply',
            cancelButtonText: 'Cancel',
            confirmButtonColor: `#${membershipDetails.color === 'yellow' ? 'EAB308' : 
                                membershipDetails.color === 'gray' ? '6B7280' : '3B82F6'}`,
            cancelButtonColor: '#d33'
        }).then((result) => {
            if (result.isConfirmed) {
                submitMembershipApplication(membershipDetails);
            }
        });
    }

    function submitMembershipApplication(membershipDetails) {
        // Create a FormData object to send the data
        const formData = new URLSearchParams();
        formData.append('membershipName', membershipDetails.name);
        formData.append('maxBooks', membershipDetails.maxBooks);
        formData.append('price', membershipDetails.price);

        fetch('${pageContext.request.contextPath}/membership', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData.toString()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.success === "true") {
                Swal.fire({
                    title: 'Success!',
                    text: 'Your membership application has been submitted successfully.',
                    icon: 'success'
                }).then(() => {
                    window.location.href = '${pageContext.request.contextPath}/studentDashboard';
                });
            } else {
                Swal.fire({
                    title: 'Error!',
                    text: data.error || 'Failed to submit application',
                    icon: 'error'
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
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