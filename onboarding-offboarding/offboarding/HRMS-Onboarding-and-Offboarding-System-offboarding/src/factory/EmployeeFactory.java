package factory;

import model.Employee;

/**
 * Factory Pattern — centralises all {@link Employee} object creation.
 *
 * <p>No other class in the system should use {@code new Employee(...)} directly.
 * All instantiation is funnelled through here so that object construction rules
 * are enforced in one place (OCP: add new employee types here without touching
 * callers).
 *
 * <p>Used exclusively by:
 * <ul>
 *   <li>{@code onboarding.handler.EmployeeCreationHandler} — chain terminal step.</li>
 *   <li>{@code onboarding.service.PreOnboardingService#createEmployeeFromCandidate}.</li>
 * </ul>
 */
public class EmployeeFactory {

    // Private constructor: static-only utility class.
    private EmployeeFactory() {}

    /**
     * Creates a minimal employee with only ID and name.
     * Suitable for provisional records or test stubs.
     */
    public static Employee createBasicEmployee(String id, String name) {
        return new Employee(id, name, null, null, null, null, "ONBOARDING");
    }

    /**
     * Creates a fully-populated employee record.
     * Primary factory method used by the Pre-Onboarding module.
     *
     * @param id      Unique employee identifier (e.g. "EMP-CAND-001").
     * @param name    Full display name.
     * @param email   Corporate email address.
     * @param dept    Department (initially "UNASSIGNED"; updated by assignRole).
     * @param role    Job role (initially "NEW_HIRE"; updated by assignRole).
     * @param contact Contact information.
     * @param status  Lifecycle status (e.g. "ONBOARDING", "ACTIVE").
     * @return A fully initialised {@link Employee}.
     */
    public static Employee createFullEmployee(String id,    String name,
                                              String email, String dept,
                                              String role,  String contact,
                                              String status) {
        return new Employee(id, name, email, dept, role, contact, status);
    }
}