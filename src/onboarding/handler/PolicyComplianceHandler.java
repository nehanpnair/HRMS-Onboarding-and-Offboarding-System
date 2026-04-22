package onboarding.handler;

import data.data.IPolicyData;
import model.model.Policy;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;
import onboarding.util.VerificationExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Handler #2 in the Pre-Onboarding chain.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Retrieves all mandatory company policies from the data layer.</li>
 *   <li>Checks each policy's compliance status in <strong>parallel</strong>
 *       using {@link VerificationExecutor}.</li>
 *   <li>Updates compliance status for each accepted policy.</li>
 *   <li>Throws {@link OnboardingException} if any policy has not been accepted;
 *       otherwise passes to the next handler.</li>
 * </ul>
 *
 * SOLID: SRP — only concerns itself with policy compliance.
 */
public class PolicyComplianceHandler extends OnboardingHandler {

    /** Status value that represents an accepted policy in the data store. */
    private static final String STATUS_ACCEPTED  = "ACCEPTED";

    /** Status value recorded when compliance is confirmed programmatically. */
    private static final String STATUS_COMPLIANT = "COMPLIANT";

    private final IPolicyData policyData;

    /**
     * @param policyData  Injected data interface for policy operations.
     *                    Must not be {@code null}.
     */
    public PolicyComplianceHandler(IPolicyData policyData) {
        if (policyData == null) {
            throw new IllegalArgumentException("IPolicyData must not be null.");
        }
        this.policyData = policyData;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Fetches all policies and checks each one in a parallel thread.
     * If any policy is not in an accepted state, an {@link OnboardingException}
     * is thrown with code {@link ErrorCodes#POLICY_NOT_ACCEPTED}.
     */
    @Override
    public void process(String candidateId) throws OnboardingException {
        System.out.println("[" + handlerName() + "] Checking policy compliance for candidate: "
                + candidateId);

        List<Policy> policies = policyData.getAllPolicies();

        if (policies == null || policies.isEmpty()) {
            // No policies configured — treat as compliant (nothing to block onboarding)
            System.out.println("[" + handlerName() + "] No policies configured — skipping compliance check.");
            passToNext(candidateId);
            return;
        }

        System.out.println("[" + handlerName() + "] Checking " + policies.size()
                + " policy/policies in parallel...");

        // Build one compliance-check task per policy
        List<Callable<Boolean>> complianceTasks = new ArrayList<>();

        for (Policy policy : policies) {
            complianceTasks.add(() -> checkPolicyCompliance(policy, candidateId));
        }

        // Parallel execution — throws if any policy check fails
        VerificationExecutor.runParallel(
                complianceTasks,
                ErrorCodes.POLICY_NOT_ACCEPTED,
                "PolicyCompliance[" + candidateId + "]");

        System.out.println("[" + handlerName() + "] All policies accepted for candidate: "
                + candidateId);

        // Chain continues
        passToNext(candidateId);
    }

    /**
     * Checks compliance for a single policy.
     *
     * <p>In production this method would query whether the candidate has
     * electronically signed/acknowledged this policy. Here it simulates that
     * check: if the policy has a valid (non-null, non-blank) ID it is considered
     * accepted, and its status is updated in the data layer.
     *
     * @param policy       The policy to check.
     * @param candidateId  Used for contextual logging.
     * @return {@code true} if the policy is accepted.
     * @throws OnboardingException if the policy is not accepted.
     */
    private boolean checkPolicyCompliance(Policy policy, String candidateId)
            throws OnboardingException {

        System.out.println("  [Thread:" + Thread.currentThread().getName()
                + "] Checking policy: " + policy.getPolicyID()
                + " for candidate: " + candidateId);

        if (policy.getPolicyID() == null || policy.getPolicyID().isBlank()) {
            throw new OnboardingException(
                    ErrorCodes.POLICY_NOT_ACCEPTED,
                    "Encountered a policy with no ID — compliance cannot be confirmed.");
        }

        /*
         * Production note: here you would call something like:
         *   candidatePolicyData.hasAccepted(candidateId, policy.getPolicyID())
         * For now we treat a well-formed policy record as accepted.
         */

        // Mark policy as compliant in the data layer
        policyData.updateComplianceStatus(policy.getPolicyID(), STATUS_COMPLIANT);

        System.out.println("  [Thread:" + Thread.currentThread().getName()
                + "] Policy " + policy.getPolicyID() + " → " + STATUS_COMPLIANT);

        return true;
    }
}
