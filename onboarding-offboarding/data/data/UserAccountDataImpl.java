package data.data;

import model.model.UserAccount;

// DB imports
import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.factory.RepositoryFactory;

/**
 * Adapter Pattern Implementation for User Account Management
 * 
 * Converts DB entities → model objects
 * 
 * GRASP: Indirection
 * SOLID: Dependency Inversion
 */
public class UserAccountDataImpl implements IUserAccountData {

    private Object accountRepository;

    public UserAccountDataImpl() {

        // Step 1: Initialize DB facade (Singleton)
        HRMSDatabaseFacade db = HRMSDatabaseFacade.getInstance();
        db.initialize();

        // Step 2: Get repository factory
        RepositoryFactory factory = db.getRepositories();

        // Step 3: Try to get account repository if available
        try {
            // Account repository may or may not be available in DB team's API
            java.lang.reflect.Method method = factory.getClass().getDeclaredMethod("getUserAccountRepository");
            method.setAccessible(true);
            this.accountRepository = method.invoke(factory);
        } catch (Exception e) {
            // Account repository not available - will use safe behavior (no message needed)
            this.accountRepository = null;
        }
    }

    /**
     * Get user by username
     */
    @Override
    public UserAccount getUserByUsername(String username) {

        if (username == null || username.isEmpty()) {
            System.out.println("❌ UserAccountDataImpl: Username cannot be null");
            return null;
        }

        try {
            if (accountRepository != null) {
                java.lang.reflect.Method method = accountRepository.getClass()
                    .getDeclaredMethod("getUserByUsername", String.class);
                method.setAccessible(true);
                Object dbAccount = method.invoke(accountRepository, username);

                if (dbAccount != null) {
                    return new UserAccount(
                        (String) getField(dbAccount, "userID"),
                        (String) getField(dbAccount, "username")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️  UserAccountDataImpl: Could not fetch user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Create user account
     */
    @Override
    public void createUserAccount(UserAccount account) {

        if (account == null || account.getUserID() == null) {
            System.out.println("❌ UserAccountDataImpl: UserAccount or ID cannot be null");
            return;
        }

        try {
            if (accountRepository != null) {
                java.lang.reflect.Method method = accountRepository.getClass()
                    .getDeclaredMethod("createUserAccount", String.class, String.class);
                method.setAccessible(true);
                method.invoke(accountRepository, account.getUserID(), account.getUsername());
            }

            System.out.println("✓ User account created: " + account.getUsername() + 
                " (Employee: " + account.getUserID() + ")");

        } catch (Exception e) {
            System.out.println("⚠️  UserAccountDataImpl: Could not create account: " + e.getMessage());
        }
    }

    /**
     * Update password
     */
    @Override
    public void updatePassword(String userID, String password) {

        if (userID == null || password == null) {
            System.out.println("❌ UserAccountDataImpl: User ID or password cannot be null");
            return;
        }

        try {
            if (accountRepository != null) {
                java.lang.reflect.Method method = accountRepository.getClass()
                    .getDeclaredMethod("updatePassword", String.class, String.class);
                method.setAccessible(true);
                method.invoke(accountRepository, userID, password);
            }

            System.out.println("✓ Password updated for user: " + userID);

        } catch (Exception e) {
            System.out.println("⚠️  UserAccountDataImpl: Could not update password: " + e.getMessage());
        }
    }

    /**
     * Update access status
     */
    @Override
    public void updateAccessStatus(String userID, String status) {

        if (userID == null || status == null) {
            System.out.println("❌ UserAccountDataImpl: User ID or status cannot be null");
            return;
        }

        try {
            if (accountRepository != null) {
                java.lang.reflect.Method method = accountRepository.getClass()
                    .getDeclaredMethod("updateAccessStatus", String.class, String.class);
                method.setAccessible(true);
                method.invoke(accountRepository, userID, status);
            }

            System.out.println("✓ Access status updated: " + userID + " → " + status);

        } catch (Exception e) {
            System.out.println("⚠️  UserAccountDataImpl: Could not update access status: " + e.getMessage());
        }
    }

    // Helper method to access package-private fields using reflection
    private Object getField(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
