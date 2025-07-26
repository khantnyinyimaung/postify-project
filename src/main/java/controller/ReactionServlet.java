package controller;

import dao.ReactionDAO;
import model.Reaction;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/react")
public class ReactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReactionDAO reactionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        reactionDAO = new ReactionDAO();
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
        String action = request.getParameter("action");
        String reactionType = request.getParameter("reactionType");

        boolean success = false;
        if ("like".equalsIgnoreCase(action)) {
            String type = (reactionType != null && !reactionType.trim().isEmpty()) ? reactionType.trim().toUpperCase() : "LIKE";
            Reaction newReaction = new Reaction();
            newReaction.setPostId(postId);
            newReaction.setUserId(currentUser.getUserId());
            newReaction.setReactionType(type);
            success = reactionDAO.addOrUpdateReaction(newReaction);
        } else if ("unlike".equalsIgnoreCase(action)) {
            success = reactionDAO.removeReaction(postId, currentUser.getUserId());
        }

        if (!success) {
            request.setAttribute("errorMessage", "Failed to perform reaction action.");
        }

        response.sendRedirect(request.getContextPath() + "/posts"); // Redirect back to posts feed
    }
}
