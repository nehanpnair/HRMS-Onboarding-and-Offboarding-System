package service;

import model.UserAccount;
import data.IUserAccountData;
import exception.AccessProvisionException;

/**
 * Handles user account creation and access control.
 * SOLID: SRP → only account-related logic
 */
public class AccountService {

    private IUserAccountData accountData;

    public AccountService(IUserAccountData accountData) {
        this.accountData = accountData;
    }

    /**
     * Create and activate account
     */
    public UserAccount createAccount(String userId, String username, String password)
            throws AccessProvisionException {

        UserAccount account = new UserAccount(userId, username);

        if (password == null || password.isEmpty()) {
            throw new AccessProvisionException("Password cannot be empty");
        }

        account.setPassword(password);
        account.activate();

        accountData.createUserAccount(account);

        System.out.println("Account created for: " + username);
        return account;
    }

    /**
     * Disable account (used in offboarding)
     */
    public void disableAccount(String userId) {
        accountData.updateAccessStatus(userId, "DISABLED");
        System.out.println("Account disabled: " + userId);
    }
}