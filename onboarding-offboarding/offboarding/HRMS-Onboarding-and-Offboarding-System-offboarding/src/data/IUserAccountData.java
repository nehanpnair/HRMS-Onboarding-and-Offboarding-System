package data;

import model.*;

// ================= USER ACCOUNT =================
public interface IUserAccountData {
    UserAccount getUserByUsername(String username);
    void createUserAccount(UserAccount account);
    void updatePassword(String userID, String password);
    void updateAccessStatus(String userID, String status);
}