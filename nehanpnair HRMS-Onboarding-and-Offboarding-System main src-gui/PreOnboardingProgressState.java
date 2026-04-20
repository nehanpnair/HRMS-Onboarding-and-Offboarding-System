package gui;

/**
 * Tracks pre-onboarding task completion state.
 * Five tasks in order:
 * 1. Send Welcome Email
 * 2. Share Pre-boarding Portal Access
 * 3. Request Document Submission
 * 4. Schedule First Day Orientation
 * 5. Prepare Workstation
 */
public class PreOnboardingProgressState {
    private boolean welcomeEmailSent = false;
    private boolean portalAccessShared = false;
    private boolean documentSubmissionRequested = false;
    private boolean orientationScheduled = false;
    private boolean workstationPrepared = false;

    // Getters
    public boolean isWelcomeEmailSent() { return welcomeEmailSent; }
    public boolean isPortalAccessShared() { return portalAccessShared; }
    public boolean isDocumentSubmissionRequested() { return documentSubmissionRequested; }
    public boolean isOrientationScheduled() { return orientationScheduled; }
    public boolean isWorkstationPrepared() { return workstationPrepared; }

    // Setters
    public void setWelcomeEmailSent(boolean v) { welcomeEmailSent = v; }
    public void setPortalAccessShared(boolean v) { portalAccessShared = v; }
    public void setDocumentSubmissionRequested(boolean v) { documentSubmissionRequested = v; }
    public void setOrientationScheduled(boolean v) { orientationScheduled = v; }
    public void setWorkstationPrepared(boolean v) { workstationPrepared = v; }

    // Check if all completed
    public boolean isAllCompleted() {
        return welcomeEmailSent && portalAccessShared && documentSubmissionRequested && 
               orientationScheduled && workstationPrepared;
    }

    // Reset
    public void reset() {
        welcomeEmailSent = false;
        portalAccessShared = false;
        documentSubmissionRequested = false;
        orientationScheduled = false;
        workstationPrepared = false;
    }
}
