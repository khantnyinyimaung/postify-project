package controller;

import dao.CommentDAO;
import dao.postDAO;
import dao.ReactionDAO;
import model.Comment;
import model.Post;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part; // Import for file upload

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID; // For unique file names

// Required for handling multipart form data (file uploads)
@WebServlet("/posts")
@jakarta.servlet.annotation.MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,     // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class PostServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private postDAO postDAO;
    private CommentDAO commentDAO;
    private ReactionDAO reactionDAO;

    // Define the directory where uploaded files will be saved
    // This should ideally be outside the webapp for production,
    // but for simplicity in this example, it's inside.
    private static final String UPLOAD_DIRECTORY = "uploads";

    @Override
    public void init() throws ServletException {
        super.init();
        postDAO = new postDAO();
        commentDAO = new CommentDAO();
        reactionDAO = new ReactionDAO();

        // Create the upload directory if it doesn't exist
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // Creates the directory including any necessary but nonexistent parent directories.
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = null;
        long currentUserId = -1L;

        if (session != null && session.getAttribute("loggedInUser") != null) {
            currentUser = (User) session.getAttribute("loggedInUser");
            currentUserId = currentUser.getUserId();
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Post> posts = postDAO.getAllPosts();

        if (posts != null) {
            for (Post post : posts) {
                List<Comment> comments = commentDAO.getCommentsForPost(post.getPostId());
                post.setComments(comments);

                int reactionCount = reactionDAO.getReactionCountForPost(post.getPostId());
                post.setReactionCount(reactionCount);

                boolean userLiked = reactionDAO.hasUserReacted(post.getPostId(), currentUserId);
                post.setUserLiked(userLiked);
            }
        }

        request.setAttribute("posts", posts);
        request.setAttribute("currentUser", currentUser);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null && session.getAttribute("loggedInUser") != null) {
            currentUser = (User) session.getAttribute("loggedInUser");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("createPost".equals(action)) {
            String content = request.getParameter("content");
            String imageUrl = null; // This will store the path to the uploaded image

            try {
                Part filePart = request.getPart("imageFile"); // Get the file part from the form
                if (filePart != null && filePart.getSize() > 0) {
                    // Generate a unique file name to prevent overwrites
                    String fileName = UUID.randomUUID().toString() + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
                    File file = new File(uploadPath + File.separator + fileName);

                    // Save the file to the server
                    try (InputStream fileContent = filePart.getInputStream()) {
                        Files.copy(fileContent, file.toPath());
                    }
                    // Store the relative path to the image in the database
                    imageUrl = request.getContextPath() + "/" + UPLOAD_DIRECTORY + "/" + fileName;
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log file upload error
                request.setAttribute("errorMessage", "Failed to upload image.");
                doGet(request, response); // Go back to feed with error
                return;
            }

            if (content != null && !content.trim().isEmpty()) {
                Post newPost = new Post();
                newPost.setUserId(currentUser.getUserId());
                newPost.setContent(content.trim());
                newPost.setImageUrl(imageUrl); // Set the uploaded image URL/path

                boolean success = postDAO.addPost(newPost);
                if (!success) {
                    request.setAttribute("errorMessage", "Failed to create post.");
                }
            } else {
                request.setAttribute("errorMessage", "Post content cannot be empty.");
            }
        } else if ("sharePost".equals(action)) {
            String content = request.getParameter("content");
            long originalPostId = Long.parseLong(request.getParameter("originalPostId"));

            if (content != null && !content.trim().isEmpty()) {
                Post sharedPost = new Post();
                sharedPost.setUserId(currentUser.getUserId());
                sharedPost.setContent(content.trim());
                sharedPost.setOriginalPostId(originalPostId);

                // For shared posts, we don't upload a new image directly.
                // The image will be fetched from the original post via PostDAO's logic.
                sharedPost.setImageUrl(null); // Ensure no new image is set for the shared post itself

                boolean success = postDAO.addPost(sharedPost);
                if (!success) {
                    request.setAttribute("errorMessage", "Failed to share post.");
                }
            } else {
                request.setAttribute("errorMessage", "Shared post content cannot be empty.");
            }
        }

        doGet(request, response);
    }
}