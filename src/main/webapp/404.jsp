<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Page Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
</head>
<body class="bg-gray-100 h-screen flex items-center justify-center">
    <div class="bg-white p-8 rounded-lg shadow-lg max-w-md w-full text-center">
        <div class="text-6xl text-yellow-500 mb-4">404</div>
        <h1 class="text-2xl font-bold text-gray-800 mb-4">Page Not Found</h1>
        <p class="text-gray-600 mb-6">The page you're looking for doesn't exist or has been moved.</p>
        <a href="/login.jsp" 
           class="bg-indigo-600 text-white px-4 py-2 rounded hover:bg-indigo-700">
            Back to Home
        </a>
    </div>
</body>
</html>