package integration;

import model.model.Employee;

/**
 * Interface exposed for ONBOARDING integration with Benefits subsystem
 *
 * Used when a new employee successfully completes onboarding
 */
public interface IOnboardingIntegration {

    /**
     * Fetch complete employee details
     */
    Employee getEmployee(String employeeID);

    /**
     * Fetch employee department (for eligibility rules)
     */
    String getEmployeeDepartment(String employeeID);

    /**
     * Notify that onboarding is completed
     * Benefits system will enroll employee into plans
     */
    void notifyOnboardingComplete(String employeeID);
}