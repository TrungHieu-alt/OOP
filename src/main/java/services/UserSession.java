// src/main/java/services/UserSession.java
package services;

public final class UserSession {
    private static final UserSession INSTANCE = new UserSession();

    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private String avatarPath;

    private UserSession() {}

    public static UserSession get() {
        return INSTANCE;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    public void clear() {
        username = null; firstName = null; lastName = null; role = null; avatarPath = null;
    }
}
