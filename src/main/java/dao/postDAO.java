package dao;

import model.Post;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class postDAO {

    /**
     * Adds a new post (or shared post) to the database.
     * @param post The Post object to add.
     * @return true if the post was added successfully, false otherwise.
     */
    public boolean addPost(Post post) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO Posts (userId, content, image_url, original_post_id, created_at) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImageUrl());
            if (post.getOriginalPostId() != null) {
                ps.setLong(4, post.getOriginalPostId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            //Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
           // post.setCreatedAt(currentTimestamp);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    post.setPostId(generatedKeys.getLong(1));
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
     * Retrieves all posts (including shared posts) for the main feed.
     * Includes username from Users table for display.
     * @return A list of Post objects, or an empty list if no posts are found.
     */
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // MODIFIED SQL: Added LEFT JOIN to fetch original post's image_url
            String sql = "SELECT p.id, p.userId, u.username, p.content, p.image_url AS current_post_image, " +
                         "p.created_at, p.updated_at, p.original_post_id, op.image_url AS original_post_image " + // Added original_post_image
                         "FROM Posts p JOIN Users u ON p.userId = u.id " +
                         "LEFT JOIN Posts op ON p.original_post_id = op.id " + // Join to get original post's details
                         "ORDER BY p.created_at DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Long originalPostId = rs.getObject("original_post_id") == null ? null : rs.getLong("original_post_id");
                String displayedImageUrl;

                // If it's a shared post, use the original post's image_url
                if (originalPostId != null) {
                    displayedImageUrl = rs.getString("original_post_image");
                } else {
                    // Otherwise, use the current post's image_url
                    displayedImageUrl = rs.getString("current_post_image");
                }

                Post post = new Post(
                    rs.getLong("id"),
                    rs.getLong("userId"),
                    rs.getString("username"),
                    rs.getString("content"),
                    displayedImageUrl, // Use the determined image URL
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    originalPostId
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return posts;
    }

    /**
     * Retrieves a single post by its ID.
     * Includes username from Users table for display.
     * @param postId The ID of the post to retrieve.
     * @return The Post object if found, null otherwise.
     */
    public Post getPostById(long postId) {
        Post post = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            // MODIFIED SQL: Added LEFT JOIN to fetch original post's image_url
            String sql = "SELECT p.id, p.userId, u.username, p.content, p.image_url AS current_post_image, " +
                         "p.created_at, p.updated_at, p.original_post_id, op.image_url AS original_post_image " + // Added original_post_image
                         "FROM Posts p JOIN Users u ON p.userId = u.id " +
                         "LEFT JOIN Posts op ON p.original_post_id = op.id " + // Join to get original post's details
                         "WHERE p.id = ?";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, postId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Long originalPostId = rs.getObject("original_post_id") == null ? null : rs.getLong("original_post_id");
                String displayedImageUrl;

                // If it's a shared post, use the original post's image_url
                if (originalPostId != null) {
                    displayedImageUrl = rs.getString("original_post_image");
                } else {
                    // Otherwise, use the current post's image_url
                    displayedImageUrl = rs.getString("current_post_image");
                }

                post = new Post(
                    rs.getLong("id"),
                    rs.getLong("userId"),
                    rs.getString("username"),
                    rs.getString("content"),
                    displayedImageUrl, // Use the determined image URL
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    originalPostId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return post;
    }

    /**
     * Retrieves all posts (original and shared) by a specific user from the database.
     * Includes username from Users table for display.
     * @param userId The ID of the user whose posts are to be retrieved.
     * @return A list of Post objects, or an empty list if no posts are found.
     */
    public List<Post> getPostsByUserId(long userId) {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // MODIFIED SQL: Added LEFT JOIN to fetch original post's image_url for both parts of UNION
            String sql = "(SELECT p.id, p.userId, u.username, p.content, p.image_url AS current_post_image, " +
                         "p.created_at, p.updated_at, p.original_post_id, op.image_url AS original_post_image " +
                         "FROM Posts p JOIN Users u ON p.userId = u.id " +
                         "LEFT JOIN Posts op ON p.original_post_id = op.id " +
                         "WHERE p.userId = ?) " +
                         "UNION ALL " +
                         "(SELECT p.id, p.userId, u.username, p.content, p.image_url AS current_post_image, " +
                         "p.created_at, p.updated_at, p.original_post_id, op.image_url AS original_post_image " +
                         "FROM Posts p JOIN Users u ON p.userId = u.id " +
                         "LEFT JOIN Posts op ON p.original_post_id = op.id " +
                         "WHERE p.original_post_id IN (SELECT id FROM Posts WHERE userId = ?)) " +
                         "ORDER BY created_at DESC";
            ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setLong(2, userId); // Set the userId for the subquery as well
            rs = ps.executeQuery();

            while (rs.next()) {
                Long originalPostId = rs.getObject("original_post_id") == null ? null : rs.getLong("original_post_id");
                String displayedImageUrl;

                // If it's a shared post, use the original post's image_url
                if (originalPostId != null) {
                    displayedImageUrl = rs.getString("original_post_image");
                } else {
                    // Otherwise, use the current post's image_url
                    displayedImageUrl = rs.getString("current_post_image");
                }

                Post post = new Post(
                    rs.getLong("id"),
                    rs.getLong("userId"),
                    rs.getString("username"),
                    rs.getString("content"),
                    displayedImageUrl, // Use the determined image URL
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    originalPostId
                );
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBConnection.closeConnection(conn);
        }
        return posts;
    }
}
