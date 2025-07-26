package model;

import java.sql.Timestamp;

public class Comment {
    private long id; // Renamed from commentId, type long
    private long cmtUser; // Renamed from userId, type long
    private String content; // Renamed from commentText
    private long postId; // Renamed from postId, type long
    private Timestamp date; // Renamed from createdAt
    private byte status; // Added status field, type byte for tinyint

    public Comment() {}

    public Comment(long id, long cmtUser, String content, long postId, Timestamp date, byte status) {
        this.id = id;
        this.cmtUser = cmtUser;
        this.content = content;
        this.postId = postId;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCmtUser() {
        return cmtUser;
    }

    public void setCmtUser(long cmtUser) {
        this.cmtUser = cmtUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    private String username; // To store the username of the commenter, fetched via join

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Comment{" +
               "id=" + id +
               ", cmtUser=" + cmtUser +
               ", content='" + content + '\'' +
               ", postId=" + postId +
               ", date=" + date +
               ", status=" + status +
               ", username='" + username + '\'' +
               '}';
    }
}
