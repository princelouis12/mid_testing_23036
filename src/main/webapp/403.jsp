<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100 h-screen flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full text-center">
        <div class="text-6xl text-red-500 mb-4">403</div>
        <h1 class="text-2xl font-bold text-gray-800 mb-4">Access Denied</h1>
        <p class="text-gray-600 mb-6">Sorry, you don't have permission to access this page.</p>
        <a href="${pageContext.request.contextPath}/login.jsp" 
           class="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">
            Back to Login
        </a>
    </div>
</body>
</html>