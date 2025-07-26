<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<title>Edit User</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
	rel="stylesheet" />
</head>
<body>
	<div class="container mt-5">
		<h2>Edit User</h2>
		<form action="${pageContext.request.contextPath}/editProfile"
			method="post">
			<!-- Hidden field for userId -->
			<input type="hidden" name="userId" value="${user.userId}" />

			<div class="mb-3">
				<label for="username" class="form-label">Username</label> <input
					type="text" class="form-control" id="username" name="username"
					value="${user.username}" required>
			</div>

			<div class="mb-3">
				<label for="email" class="form-label">Email</label> <input
					type="email" class="form-control" id="email" name="email"
					value="${user.email}" required>
			</div>

			<div class="mb-3" id="passwordToggle">
				<button type="button" class="btn btn-warning"
					onclick="togglePasswordFields()">Change Password</button>
			</div>

			<div class="mb-3">
				<label for="fullName" class="form-label">Full Name</label> <input
					type="text" class="form-control" id="fullName" name="fullName"
					value="${user.fullName}" required>
			</div>

			<div class="mb-3">
				<label for="createdAt" class="form-label">Created At</label> 
				<input
					type="text" class="form-control" id="createdAt" name="createdAt"
					value="${user.createdAt}" readonly>
			</div>

			<button type="submit" class="btn btn-primary">Update User</button>
			<a href="${pageContext.request.contextPath}/profile?userId=${user.userId}" class="btn btn-secondary">Cancel</a>
		</form>
	</div>

</body>
</html>

