package model;

import java.sql.Timestamp;
import java.util.List;

public class Post {
    private long postId;
    private long userId;
    private String username;
    private String content;
    private String imageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Long originalPostId; // Changed to Long for nullable

    // Transient fields to hold associated data, not directly stored in Post table
    private List<Comment> comments;
    private int reactionCount;
    private boolean userLiked;

    public Post() {}

    public Post(long postId, long userId, String username, String content, String imageUrl, Timestamp createdAt, Timestamp updatedAt, Long originalPostId) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.originalPostId = originalPostId;
    }

    // Getters and Setters
    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getOriginalPostId() {
        return originalPostId;
    }

    public void setOriginalPostId(Long originalPostId) {
        this.originalPostId = originalPostId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public int getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(int reactionCount) {
        this.reactionCount = reactionCount;
    }

    public boolean isUserLiked() {
        return userLiked;
    }

    public void setUserLiked(boolean userLiked) {
        this.userLiked = userLiked;
    }

    @Override
    public String toString() {
        return "Post{" +
               "postId=" + postId +
               ", userId=" + userId +
               ", username='" + username + '\'' +
               ", content='" + content + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               ", createdAt=" + createdAt +
               ", originalPostId=" + originalPostId +
               ", reactionCount=" + reactionCount +
               ", userLiked=" + userLiked +
               '}';
    }
}
