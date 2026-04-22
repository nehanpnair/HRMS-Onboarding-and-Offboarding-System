package model;

public class UserAccount {
    private String userID;
    private String username;

    public UserAccount(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }
}