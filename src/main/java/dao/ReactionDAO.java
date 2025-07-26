package dao;

import model.Reaction;
import java.sql.*;

public class ReactionDAO {

    /**
     * Adds a new reaction or updates an existing one.
     * If a reaction by the same user on the same post already exists, it updates the type.
     * @param reaction The Reaction object to add/update.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean addOrUpdateReaction(Reaction reaction) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            // Check if reaction already exists
            String checkSql = "SELECT reaction_id FROM Reactions WHERE post_id = ? AND user_id = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setLong(1, reaction.getPostId());
            ps.setLong(2, reaction.getUserId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Reaction exists, update it
                long reactionId = rs.getLong("reaction_id");
                String updateSql = "UPDATE Reactions SET reaction_type = ? WHERE reaction_id = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setString(1, reaction.getReactionType());
                ps.setLong(2, reactionId);
                return ps.executeUpdate() > 0;
            } else {
                // Reaction does not exist, insert new one
                String insertSql = "INSERT INTO Reactions (post_id, user_id, reaction_type) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, reaction.getPostId());
                ps.setLong(2, reaction.getUserId());
                ps.setString(3, reaction.getReactionType());
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        reaction.setReactionId(generatedKeys.getLong(1));
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    /**
     * Removes a reaction from the database.
     * @param postId The ID of the post.
     * @param userId The ID of the user.
     * @return true if the reaction was removed successfully, false otherwise.
     */
    public boolean removeReaction(long postId, long userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Reactions WHERE post_id = ? AND user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, postId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    /**
     * Gets the count of reactions for a specific post.
     * @param postId The ID of the post.
     * @return The number of reactions.
     */
    public int getReactionCountForPost(long postId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS reaction_count FROM Reactions WHERE post_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, postId);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("reaction_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return count;
    }

    /**
     * Checks if a user has reacted to a specific post.
     * @param postId The ID of the post.
     * @param userId The ID of the user.
     * @return true if the user has reacted, false otherwise.
     */
    public boolean hasUserReacted(long postId, long userId) {
        boolean reacted = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) FROM Reactions WHERE post_id = ? AND user_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, postId);
            ps.setLong(2, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                reacted = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return reacted;
    }
}
