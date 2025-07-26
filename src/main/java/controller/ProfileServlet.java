package controller;

import dao.CommentDAO;
import dao.postDAO;
import dao.ReactionDAO;
import dao.UserDAO;
import model.Comment;
import model.Post;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private postDAO postDAO;
    private CommentDAO commentDAO;
    private ReactionDAO reactionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        postDAO = new postDAO();
        commentDAO = new CommentDAO();
        reactionDAO = new ReactionDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = null;
        long currentUserId = -1L; // Default if no user is logged in

        if (session != null && session.getAttribute("loggedInUser") != null) {
            currentUser = (User) session.getAttribute("loggedInUser");
            currentUserId = currentUser.getUserId();
        } else {
            // If no user is logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Get the userId from the request parameter for the profile to be viewed
        String userIdParam = request.getParameter("userId");
        long profileUserId = -1;

        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                profileUserId = Long.parseLong(userIdParam);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid user ID format.");
                request.getRequestDispatcher("/posts").forward(request, response);
                return;
            }
        } else {
            // If no userId parameter, view current user's profile
            profileUserId = currentUser.getUserId();
        }

        // Fetch the user whose profile is being viewed
        User profileUser = userDAO.getUserById(profileUserId);

        if (profileUser == null) {
            request.setAttribute("errorMessage", "User not found.");
            request.getRequestDispatcher("/posts").forward(request, response);
            return;
        }

        // Fetch posts (original and shared) by the profile user
        List<Post> userPosts = postDAO.getPostsByUserId(profileUserId);

        // Enrich posts with comments and reactions for display on the profile page
        if (userPosts != null) {
            for (Post post : userPosts) {
                List<Comment> comments = commentDAO.getCommentsForPost(post.getPostId());
                post.setComments(comments);

                int reactionCount = reactionDAO.getReactionCountForPost(post.getPostId());
                post.setReactionCount(reactionCount);

                // Check if the current logged-in user has reacted to this specific post
                boolean userLiked = reactionDAO.hasUserReacted(post.getPostId(), currentUserId);
                post.setUserLiked(userLiked);
            }
        }

        request.setAttribute("profileUser", profileUser);
        request.setAttribute("userPosts", userPosts);
        request.setAttribute("currentUser", currentUser); // Pass current user for conditional display

        // Forward to the profile JSP
        request.getRequestDispatcher("/profile.jsp").forward(request, response); // Adjust path as per your JSP location
    }
}
