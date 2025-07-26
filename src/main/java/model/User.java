package model;

import java.sql.Timestamp;

public class User {
    private long userId;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private Timestamp createdAt;
    private String profilePictureUrl; // Added: Field for profile picture URL

    // Constructors
    public User() {}

    public User(long userId, String username, String email, String passwordHash, String fullName, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.profilePictureUrl = null; // Default to null if not provided
    }

    // New Constructor including profilePictureUrl
    public User(long userId, String username, String email, String passwordHash, String fullName, Timestamp createdAt, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for profilePictureUrl
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", passwordHash='" + passwordHash + '\'' +
               ", fullName='" + fullName + '\'' +
               ", createdAt=" + createdAt +
               ", profilePictureUrl='" + profilePictureUrl + '\'' +
               '}';
    }
}