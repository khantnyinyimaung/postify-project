package controller;

import java.io.IOException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

@WebServlet("/editProfile")
public class EditProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("loggedInUser");

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Optional: Fetch fresh user data from DB
        User fullUser = userDAO.getUserById(currentUser.getUserId());
        request.setAttribute("user", fullUser);

        request.getRequestDispatcher("/edit.jsp").forward(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long userId = Long.parseLong(request.getParameter("userId"));
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("passwordHash");
        String fullName = request.getParameter("fullName");

        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setFullName(fullName);

        userDAO.updateUser(user);

        HttpSession session = request.getSession();
        session.setAttribute("loggedInUser", user); // optional

        // Redirect to updated profile
        response.sendRedirect(request.getContextPath() + "/profile?userId=" + userId);
    }

}

