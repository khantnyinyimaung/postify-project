package dao;

import model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    /**
     * Adds a new comment to the database.
     * @param comment The Comment object to add.
     * @return true if the comment was added successfully, false otherwise.
     */
    public boolean addComment(Comment comment) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO comments (postId, cmtUser, content, date, status) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPostId());
            ps.setLong(2, comment.getCmtUser());
            ps.setString(3, comment.getContent());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setByte(5, (byte) 1);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    comment.setId(generatedKeys.getLong(1));
                }
                return true;
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
     * Retrieves all comments for a specific post.
     * @param postId The ID of the post.
     * @return A list of Comment objects for the given post.
     */
    public List<Comment> getCommentsForPost(long postId) {
        List<Comment> comments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT c.id, c.postId, c.cmtUser, u.username, c.content, c.date, c.status " +
                         "FROM comments c JOIN Users u ON c.cmtUser = u.id WHERE c.postId = ? ORDER BY c.date ASC";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, postId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment(
                    rs.getLong("id"),
                    rs.getLong("cmtUser"),
                    rs.getString("content"),
                    rs.getLong("postId"),
                    rs.getTimestamp("date"),
                    rs.getByte("status")
                );
                comment.setUsername(rs.getString("username"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return comments;
    }
}
