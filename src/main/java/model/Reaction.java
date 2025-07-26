package model;

import java.sql.Timestamp;

public class Reaction {
    private long reactionId;
    private long postId;
    private long userId;
    private String reactionType;
    private Timestamp createdAt;

    public Reaction() {}

    public Reaction(long reactionId, long postId, long userId, String reactionType, Timestamp createdAt) {
        this.reactionId = reactionId;
        this.postId = postId;
        this.userId = userId;
        this.reactionType = reactionType;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getReactionId() {
        return reactionId;
    }

    public void setReactionId(long reactionId) {
        this.reactionId = reactionId;
    }

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

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reaction{" +
               "reactionId=" + reactionId +
               ", postId=" + postId +
               ", userId=" + userId +
               ", reactionType='" + reactionType + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}
