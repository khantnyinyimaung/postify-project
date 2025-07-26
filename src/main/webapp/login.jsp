<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Postify</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f0f2f5;
        }
    </style>
</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
    <div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <h2 class="text-3xl font-bold text-center text-blue-600 mb-6">Login to Postify</h2>

        <c:if test="${errorMessage != null}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-xl relative mb-4" role="alert">
                <strong class="font-bold">Error!</strong>
                <span class="block sm:inline">${errorMessage}</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth" method="post" class="space-y-5">
            <input type="hidden" name="action" value="login">
            <div>
                <label for="username" class="block text-gray-700 text-sm font-semibold mb-2">Username:</label>
                <input type="text" id="username" name="username" required
                       class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                       placeholder="Enter your username">
            </div>
            <div>
                <label for="password" class="block text-gray-700 text-sm font-semibold mb-2">Password:</label>
                <input type="password" id="password" name="password" required
                       class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                       placeholder="Enter your password">
            </div>
            <button type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition ease-in-out duration-200">
                Log In
            </button>
        </form>

        <p class="text-center text-gray-600 text-sm mt-6">
            Don't have an account? <a href="${pageContext.request.contextPath}/register.jsp" class="text-blue-600 hover:underline font-medium">Sign Up</a>
        </p>
    </div>
</body>
</html>