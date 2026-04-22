package onboarding.service;

import model.model.Candidate;
import model.model.Employee;
import onboarding.exception.OnboardingException;

/**
 * Service contract for the Pre-Onboarding module.
 *
 * <p>Defines the two public entry points that external subsystems (e.g. the
 * Customization team's workflow engine) and {@code RoleAccessProxy} interact with.
 *
 * <p>Separating the interface from the implementation ({@link PreOnboardingService})
 * enables:
 * <ul>
 *   <li>Proxy Pattern — {@code RoleAccessProxy} wraps this interface.</li>
 *   <li>SOLID DIP — callers depend on the abstraction, not the concrete class.</li>
 *   <li>Easy mocking in tests.</li>
 * </ul>
 */
public interface IPreOnboardingService {

    /**
     * Runs the full pre-onboarding validation pipeline for the given candidate
     * and, if all steps pass, creates the corresponding {@link Employee} record.
     *
     * <p>Internally builds and executes the Chain of Responsibility:
     * <pre>
     *   DocumentVerification → PolicyCompliance → ReferenceCheck → EmployeeCreation
     * </pre>
     *
     * @param candidateId  Unique ID of the candidate to onboard.
     * @return {@code true} if the full pipeline succeeded; {@code false} if an
     *         error was handled internally (the caller should also check logs).
     * @throws OnboardingException if validation fails and re-raising is preferred.
     */
    boolean startPreOnboarding(String candidateId) throws OnboardingException;

    /**
     * Validates a candidate through the document, policy, and reference steps
     * <em>without</em> creating an employee record.
     *
     * <p>Useful for partial checks (e.g. real-time form validation) or dry-run
     * flows that want to surface issues before committing to employee creation.
     *
     * @param candidateId  Unique ID of the candidate to validate.
     * @return {@code true} if all validation steps pass.
     * @throws OnboardingException if any validation step fails.
     */
    boolean validateCandidate(String candidateId) throws OnboardingException;

    /**
     * Converts a fully-validated {@link Candidate} into an {@link Employee}
     * using the {@code EmployeeFactory}.
     *
     * <p>Exposed here so the integration interface and the proxy can call it
     * directly when a candidate has already been validated by an earlier step.
     *
     * @param candidate  A non-null, fully-populated candidate object.
     * @return The newly created {@link Employee}.
     * @throws OnboardingException if conversion fails (e.g. missing required fields).
     */
    Employee createEmployeeFromCandidate(Candidate candidate) throws OnboardingException;
}
