package controller;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            User user = userDAO.getUserByUsername(username);

            if (user != null && user.getPasswordHash().equals(password)) {
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUser", user);
                response.sendRedirect(request.getContextPath() + "/posts");
            } else {
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } else if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else if ("register".equals(action)) {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String fullName = request.getParameter("fullName");

            if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required for registration.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            String hashedPassword = password;

            User newUser = new User();
            newUser.setUsername(username.trim());
            newUser.setEmail(email.trim());
            newUser.setPasswordHash(hashedPassword);
            newUser.setFullName(fullName != null ? fullName.trim() : "");

            boolean success = userDAO.addUser(newUser);

            if (success) {
//                HttpSession session = request.getSession();
//                session.setAttribute("loggedInUser", newUser);
//                response.sendRedirect(request.getContextPath() + "/posts");
                  request.getRequestDispatcher("/login.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Registration failed. Username or email might already be taken.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } 
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            doPost(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
