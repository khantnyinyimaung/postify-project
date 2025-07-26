<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Postify</title>
    <!-- Tailwind CSS CDN -->
<script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f0f2f5; /* Light gray background */
        }
        .post-card {
            background-color: #ffffff;
            border-radius: 0.75rem; /* rounded-xl */
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* shadow-md */
            margin-bottom: 1.5rem; /* mb-6 */
        }
        .comment-section {
            background-color: #f9f9f9;
            border-top: 1px solid #e2e8f0; /* border-t border-gray-200 */
        }
        .action-button {
            transition: all 0.2s ease-in-out;
        }
        .action-button:hover {
            transform: translateY(-2px);
        }
        .modal {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1000; /* Sit on top */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
            justify-content: center;
            align-items: center;
        }
        .modal-content {
            background-color: #fefefe;
            padding: 20px;
            border-radius: 0.75rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            max-width: 500px;
            width: 90%;
        }
        .close-button {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }
        .close-button:hover,
        .close-button:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
    </style>
</head>
<body class="bg-gray-100 flex flex-col items-center min-h-screen">
    <!-- Navbar -->
    <nav class="bg-white shadow-md p-4 w-full max-w-4xl rounded-b-xl mb-6">
        <div class="container mx-auto flex justify-between items-center">
            <a href="${pageContext.request.contextPath}/posts" class="text-2xl font-bold text-blue-600">Postify</a>
            <div class="flex items-center space-x-4">
                <c:if test="${currentUser != null}">
                    <a href="${pageContext.request.contextPath}/profile?userId=${currentUser.userId}"
                       class="text-gray-700 hover:text-blue-600 hover:underline">
                        Welcome, <strong>${currentUser.username}</strong>!
                    </a>
                    <form action="${pageContext.request.contextPath}/auth" method="post">
                        <input type="hidden" name="action" value="logout">
                        <button type="submit" class="bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition ease-in-out duration-200">
                            Logout
                        </button>
                    </form>
                </c:if>
                <c:if test="${currentUser == null}">
                    <a href="${pageContext.request.contextPath}/login.jsp" class="text-blue-600 hover:underline">Login</a>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition ease-in-out duration-200">Sign Up</a>
                </c:if>
            </div>
        </div>
    </nav>

    <main class="container mx-auto p-4 max-w-4xl w-full">

        <c:if test="${errorMessage != null}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-xl relative mb-6" role="alert">
                <strong class="font-bold">Error!</strong>
                <span class="block sm:inline">${errorMessage}</span>
            </div>
        </c:if>

        <c:if test="${currentUser != null}">
            <!-- Create New Post Form -->
            <div class="post-card p-6 mb-6">
                <h2 class="text-xl font-semibold text-gray-800 mb-4">Create New Post</h2>
                <%-- MODIFIED: Added enctype="multipart/form-data" --%>
                <form action="${pageContext.request.contextPath}/posts" method="post" class="space-y-4" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="createPost">
                    <div>
                        <textarea name="content" rows="4" placeholder="What's on your mind, ${currentUser.username}?"
                                  class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
                    </div>
                    <div>
                        <label for="imageFile" class="block text-sm font-medium text-gray-700 mb-1">Upload Photo (Optional):</label>
                        <%-- MODIFIED: Changed to type="file" --%>
                        <input type="file" name="imageFile" id="imageFile" accept="image/*"
                               class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100">
                    </div>
                    <button type="submit"
                            class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition ease-in-out duration-200 action-button">
                        <i class="fas fa-paper-plane mr-2"></i> Post
                    </button>
                </form>
            </div>
        </c:if>

        <!-- Posts Feed -->
        <div class="space-y-6">
            <c:forEach var="post" items="${posts}">
                <div class="post-card p-6">
                    <div class="flex items-center mb-4">
                        <div class="w-10 h-10 bg-gray-300 rounded-full flex items-center justify-center text-gray-600 font-bold text-lg">
                            <i class="fas fa-user"></i>
                        </div>
                        <div class="ml-3">
                            <a href="${pageContext.request.contextPath}/profile?userId=${post.userId}"
                               class="font-semibold text-gray-800 hover:text-blue-600 hover:underline">
                                ${post.username}
                            </a>
                            <p class="text-sm text-gray-500">
                                <fmt:formatDate value="${post.createdAt}" pattern="MMM dd,yyyy 'at' hh:mm a" />
                                <c:if test="${post.originalPostId != null}">
                                    <span class="text-blue-500 text-xs ml-2"> (Shared Post)</span>
                                </c:if>
                            </p>
                        </div>
                    </div>

                    <p class="text-gray-700 mb-4">${post.content}</p>

                    <c:if test="${post.imageUrl != null && !post.imageUrl.isEmpty()}">
                        <div class="mb-4 rounded-lg overflow-hidden">
                            <%-- Removed onerror attribute and added proper conditional rendering --%>
                            <img src="${post.imageUrl}" alt="Post Image" class="w-full h-auto object-cover max-h-96">
                        </div>
                    </c:if>

                    <div class="flex items-center text-gray-500 text-sm mb-4">
                        <span class="mr-4"><i class="fas fa-thumbs-up"></i> ${post.reactionCount} Likes</span>
                        <span><i class="fas fa-comment-alt"></i> ${post.comments.size()} Comments</span>
                    </div>

                    <div class="flex border-t border-gray-200 pt-4 space-x-4">
                        <!-- Reaction Button -->
                        <form action="${pageContext.request.contextPath}/react" method="post" class="flex-1">
                            <input type="hidden" name="postId" value="${post.postId}">
                            <c:choose>
                                <c:when test="${post.userLiked}">
                                    <input type="hidden" name="action" value="unlike">
                                    <button type="submit" class="w-full bg-blue-500 text-white font-semibold py-2 rounded-lg flex items-center justify-center action-button">
                                        <i class="fas fa-thumbs-up mr-2"></i> Liked
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="action" value="like">
                                    <button type="submit" class="w-full text-blue-600 border border-blue-600 hover:bg-blue-100 font-semibold py-2 rounded-lg flex items-center justify-center action-button">
                                        <i class="far fa-thumbs-up mr-2"></i> Like
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form>

                        <!-- Comment Button (opens modal) -->
                        <button onclick="openCommentModal(${post.postId})"
                                class="flex-1 text-gray-600 border border-gray-300 hover:bg-gray-100 font-semibold py-2 rounded-lg flex items-center justify-center action-button">
                            <i class="far fa-comment-alt mr-2"></i> Comment
                        </button>

                        <!-- Share Button (opens modal) -->
                        <button onclick="openShareModal(${post.postId}, '${post.content}')"
                                class="flex-1 text-gray-600 border border-gray-300 hover:bg-gray-100 font-semibold py-2 rounded-lg flex items-center justify-center action-button">
                            <i class="fas fa-share mr-2"></i> Share
                        </button>
                    </div>

                    <!-- Comments Section -->
                    <div class="comment-section p-4 mt-6 rounded-b-xl">
                        <h3 class="text-md font-semibold text-gray-700 mb-3">Comments:</h3>
                        <c:choose>
                            <c:when test="${not empty post.comments}">
                                <div class="space-y-3">
                                    <c:forEach var="comment" items="${post.comments}">
                                        <div class="bg-gray-50 p-3 rounded-lg">
                                            <div class="flex items-center mb-1">
                                                <strong class="text-sm text-gray-800 mr-2">
                                                    <a href="${pageContext.request.contextPath}/profile?userId=${comment.cmtUser}"
                                                       class="hover:text-blue-600 hover:underline">
                                                        ${comment.username}
                                                    </a>
                                                </strong>
                                                <span class="text-xs text-gray-500">
                                                    <fmt:formatDate value="${comment.date}" pattern="MMM dd,yyyy 'at' hh:mm a" />
                                                </span>
                                            </div>
                                            <p class="text-sm text-gray-700">${comment.content}</p>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-500 text-sm">No comments yet. Be the first to comment!</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </main>

    <!-- Comment Modal -->
    <div id="commentModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeCommentModal()">&times;</span>
            <h2 class="text-xl font-semibold text-gray-800 mb-4">Add a Comment</h2>
            <form action="${pageContext.request.contextPath}/comment" method="post" class="space-y-4">
                <input type="hidden" name="postId" id="commentModalPostId">
                <div>
                    <textarea name="commentText" rows="3" placeholder="Write your comment..."
                              class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
                </div>
                <button type="submit"
                        class="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition ease-in-out duration-200 action-button">
                    <i class="fas fa-comment-alt mr-2"></i> Submit Comment
                </button>
            </form>
        </div>
    </div>

    <!-- Share Modal -->
    <div id="shareModal" class="modal">
        <div class="modal-content">
            <span class="close-button" onclick="closeShareModal()">&times;</span>
            <h2 class="text-xl font-semibold text-gray-800 mb-4">Share Post</h2>
            <form action="${pageContext.request.contextPath}/posts" method="post" class="space-y-4">
                <input type="hidden" name="action" value="sharePost">
                <input type="hidden" name="originalPostId" id="shareModalOriginalPostId">
                <div>
                    <label for="shareContent" class="block text-gray-700 text-sm font-bold mb-2">Your thought on this post:</label>
                    <textarea name="content" id="shareContent" rows="4"
                              placeholder="Add your thoughts or simply share this post..."
                              class="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
                </div>
                <button type="submit"
                        class="w-full bg-green-600 hover:bg-green-700 text-white font-semibold py-3 rounded-lg shadow-md hover:shadow-lg transition ease-in-out duration-200 action-button">
                    <i class="fas fa-share mr-2"></i> Share Post
                </button>
            </form>
        </div>
    </div>


    <script>
        // Modal functions
        function openCommentModal(postId) {
            document.getElementById('commentModalPostId').value = postId;
            document.getElementById('commentModal').style.display = 'flex';
        }

        function closeCommentModal() {
            document.getElementById('commentModal').style.display = 'none';
        }

        function openShareModal(postId, postContent) {
            document.getElementById('shareModalOriginalPostId').value = postId;
            // Optionally pre-fill the share content with the original post's content
            document.getElementById('shareContent').value = "Shared: \"" + postContent.substring(0, 100) + (postContent.length > 100 ? "..." : "") + "\"";
            document.getElementById('shareModal').style.display = 'flex';
        }

        function closeShareModal() {
            document.getElementById('shareModal').style.display = 'none';
        }

        // Close modals if clicked outside
        window.onclick = function(event) {
            if (event.target == document.getElementById('commentModal')) {
                closeCommentModal();
            }
            if (event.target == document.getElementById('shareModal')) {
                closeShareModal();
            }
        }
    </script>
</body>
</html>
