package integration;

/**
 * Interface exposed for OFFBOARDING integration with Benefits subsystem
 *
 * Used when an employee exits the organization
 */
public interface IOffboardingIntegration {

    /**
     * Notify that offboarding is initiated/completed
     * Benefits system will deactivate plans and process exits
     */
    void notifyOffboardingInitiated(String employeeID);
}