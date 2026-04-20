package model;

public class UserAccount {
    private String userID;
    private String username;
    private String password;
    private boolean active;

    public UserAccount(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void activate() {
        this.active = true;
    }
}