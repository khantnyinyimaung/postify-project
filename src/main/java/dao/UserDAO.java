package dao;

import model.User;
import java.sql.*;

public class UserDAO {

    public User getUserByUsername(String username) {
        User user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id, username, email, password_hash, full_name, created_at, profile_picture_url FROM Users WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getTimestamp("created_at"),
                    rs.getString("profile_picture_url")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return user;
    }

    /**
     * Retrieves a user by their ID.
     * @param userId The ID of the user to retrieve.
     * @return The User object if found, null otherwise.
     */
    public User getUserById(long userId) {
        User user = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id, username, email, password_hash, full_name, created_at FROM Users WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return user;
    }

    public boolean addUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            // Added profile_picture_url to the INSERT statement
            String sql = "INSERT INTO Users (username, email, password_hash, full_name, profile_picture_url) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getProfilePictureUrl()); // Set profile picture URL (can be null initially)
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) {
				ps.close();
			} } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return false;
    }
    
    public boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE Users SET username = ?, email = ?, full_name = ?, password_hash = ?, created_at = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getPasswordHash());
            ps.setTimestamp(5, user.getCreatedAt()); // Pass this as a java.sql.Timestamp
            ps.setLong(6, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return false;
    }


    public boolean validateUser(String username, String passwordHash) {
        User user = getUserByUsername(username);
        return user != null && user.getPasswordHash().equals(passwordHash);
    }
}
