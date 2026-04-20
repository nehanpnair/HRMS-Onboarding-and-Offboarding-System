package gui;

/**
 * Tracks onboarding step completion state.
 */
public class OnboardingProgressState {
    private boolean roleAssigned = false;
    private boolean workflowStarted = false;
    private boolean formsLoaded = false;
    private boolean assetsAllocated = false;
    private boolean accountCreated = false;
    private boolean trainingAssigned = false;
    private boolean accessControlled = false;

    // Getters
    public boolean isRoleAssigned() { return roleAssigned; }
    public boolean isWorkflowStarted() { return workflowStarted; }
    public boolean isFormsLoaded() { return formsLoaded; }
    public boolean isAssetsAllocated() { return assetsAllocated; }
    public boolean isAccountCreated() { return accountCreated; }
    public boolean isTrainingAssigned() { return trainingAssigned; }
    public boolean isAccessControlled() { return accessControlled; }

    // Setters
    public void setRoleAssigned(boolean v) { roleAssigned = v; }
    public void setWorkflowStarted(boolean v) { workflowStarted = v; }
    public void setFormsLoaded(boolean v) { formsLoaded = v; }
    public void setAssetsAllocated(boolean v) { assetsAllocated = v; }
    public void setAccountCreated(boolean v) { accountCreated = v; }
    public void setTrainingAssigned(boolean v) { trainingAssigned = v; }
    public void setAccessControlled(boolean v) { accessControlled = v; }

    // Check if all completed (including access control)
    public boolean isAllCompleted() {
        return roleAssigned && workflowStarted && formsLoaded && 
               assetsAllocated && accountCreated && trainingAssigned && accessControlled;
    }

    // Reset
    public void reset() {
        roleAssigned = false;
        workflowStarted = false;
        formsLoaded = false;
        assetsAllocated = false;
        accountCreated = false;
        trainingAssigned = false;
        accessControlled = false;
    }
}
