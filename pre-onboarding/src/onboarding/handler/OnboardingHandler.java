package onboarding.handler;

import onboarding.exception.OnboardingException;

/**
 * Abstract base handler for the Pre-Onboarding Chain of Responsibility.
 *
 * <p>Each concrete handler in the chain is responsible for exactly one
 * validation or transformation step. If the step passes, the handler
 * delegates to the next handler in the chain. If it fails, it throws
 * an {@link OnboardingException} that propagates up to
 * {@code PreOnboardingService}, stopping the pipeline immediately.
 *
 * <p>Chain setup (configured in {@code PreOnboardingService}):
 * <pre>
 *   DocumentVerification → PolicyCompliance → ReferenceCheck → EmployeeCreation
 * </pre>
 *
 * SOLID:
 * <ul>
 *   <li>OCP  — new handlers can be inserted without changing existing ones.</li>
 *   <li>SRP  — each subclass handles exactly one concern.</li>
 *   <li>LSP  — all subclasses honour the {@link #process(String)} contract.</li>
 * </ul>
 */
public abstract class OnboardingHandler {

    /**
     * The next handler in the chain.
     * {@code null} means this is the last link.
     */
    private OnboardingHandler next;

    /**
     * Links the next handler after this one.
     *
     * @param next  The handler to invoke after this one succeeds.
     *              Pass {@code null} to mark this as the terminal handler.
     * @return The {@code next} handler, enabling fluent chaining:
     *         <pre>docHandler.setNext(policyHandler).setNext(refHandler)</pre>
     */
    public OnboardingHandler setNext(OnboardingHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Core processing method. Subclasses implement their validation logic here.
     *
     * <p>Convention: if this handler's step passes, the implementation should
     * call {@link #passToNext(String)} so the chain continues. If it fails,
     * throw {@link OnboardingException}.
     *
     * @param candidateId  The unique ID of the candidate being processed.
     * @throws OnboardingException if this handler's validation fails.
     */
    public abstract void process(String candidateId) throws OnboardingException;

    /**
     * Passes control to the next handler, if one exists.
     *
     * <p>Concrete handlers call this at the end of a successful {@link #process}
     * to continue the chain. If there is no next handler the pipeline ends here.
     *
     * @param candidateId  Forwarded candidate ID.
     * @throws OnboardingException propagated from the downstream handler.
     */
    protected void passToNext(String candidateId) throws OnboardingException {
        if (next != null) {
            next.process(candidateId);
        }
    }

    /**
     * Returns the handler's simple class name for logging purposes.
     */
    protected String handlerName() {
        return getClass().getSimpleName();
    }
}
