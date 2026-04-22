package onboarding.handler;

import data.data.IReferenceCheckData;
import model.model.ReferenceCheck;
import onboarding.exception.ErrorCodes;
import onboarding.exception.OnboardingException;

/**
 * Handler #3 in the Pre-Onboarding chain.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Looks up the reference check record for the candidate.</li>
 *   <li>Validates that the reference has been successfully verified.</li>
 *   <li>Updates the reference status on success.</li>
 *   <li>Throws {@link OnboardingException} if the reference check is absent or
 *       failed; otherwise passes to the next handler (EmployeeCreation).</li>
 * </ul>
 *
 * <p>Reference checks are inherently sequential (there is typically one record
 * per candidate), so no multithreading is applied here.
 *
 * SOLID: SRP — only concerns itself with reference verification.
 */
public class ReferenceCheckHandler extends OnboardingHandler {

    /** Status recorded when the reference check passes. */
    private static final String STATUS_VERIFIED = "VERIFIED";

    /**
     * Status value that the incoming reference record must carry to be
     * considered as having already passed an external check. If the
     * record does not carry this status the handler performs its own
     * verification and updates it.
     */
    private static final String STATUS_PASSED = "PASSED";

    private final IReferenceCheckData referenceCheckData;

    /**
     * @param referenceCheckData  Injected data interface for reference records.
     *                             Must not be {@code null}.
     */
    public ReferenceCheckHandler(IReferenceCheckData referenceCheckData) {
        if (referenceCheckData == null) {
            throw new IllegalArgumentException("IReferenceCheckData must not be null.");
        }
        this.referenceCheckData = referenceCheckData;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Fetches the reference check record for {@code candidateId} and
     * validates it. Throws if no record exists or the check has failed.
     */
    @Override
    public void process(String candidateId) throws OnboardingException {
        System.out.println("[" + handlerName() + "] Running reference check for candidate: "
                + candidateId);

        ReferenceCheck reference = referenceCheckData.getReferenceByCandidate(candidateId);

        // Guard: no reference record exists yet
        if (reference == null) {
            throw new OnboardingException(
                    ErrorCodes.REFERENCE_NOT_FOUND,
                    "No reference check record found for candidate: " + candidateId);
        }

        System.out.println("[" + handlerName() + "] Found reference: "
                + reference.getReferenceID() + " — validating...");

        /*
         * Production note: here you would call an external background-check
         * provider API or query a verification-status column. We treat a
         * non-null referenceID as a successful external check result.
         *
         * If the external result is FAILED, throw OnboardingException.
         * Example:
         *   if ("FAILED".equalsIgnoreCase(reference.getVerificationStatus())) {
         *       throw new OnboardingException(ErrorCodes.REFERENCE_CHECK_FAILED, ...);
         *   }
         */

        // Mark reference as verified in the data layer
        referenceCheckData.updateReferenceStatus(reference.getReferenceID(), STATUS_VERIFIED);

        System.out.println("[" + handlerName() + "] Reference check passed for candidate: "
                + candidateId + " (referenceID: " + reference.getReferenceID() + ")");

        // Chain continues to EmployeeCreationHandler
        passToNext(candidateId);
    }
}
