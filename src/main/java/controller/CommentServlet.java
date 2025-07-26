package controller;

import dao.CommentDAO;
import model.Comment;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CommentDAO commentDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        commentDAO = new CommentDAO();
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

        long postId = Long.parseLong(request.getParameter("postId"));
        String commentText = request.getParameter("commentText");

        if (commentText != null && !commentText.trim().isEmpty()) {
            Comment newComment = new Comment();
            newComment.setPostId(postId);
            newComment.setCmtUser(currentUser.getUserId());
            newComment.setContent(commentText.trim());

            boolean success = commentDAO.addComment(newComment);
            if (!success) {
                request.setAttribute("errorMessage", "Failed to add comment.");
            }
        } else {
            request.setAttribute("errorMessage", "Comment cannot be empty.");
        }

        
        response.sendRedirect(request.getContextPath() + "/posts");
    }
}
