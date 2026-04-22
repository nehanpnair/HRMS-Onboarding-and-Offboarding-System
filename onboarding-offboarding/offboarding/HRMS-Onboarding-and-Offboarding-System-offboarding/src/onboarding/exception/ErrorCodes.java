package onboarding.exception;

/**
 * Error code constants for the Pre-Onboarding module.
 *
 * Each constant maps to a specific failure condition in the validation pipeline.
 * Referenced by OnboardingException and all Chain-of-Responsibility handlers.
 *
 * SOLID: Open/Closed — add new codes here without touching handler logic.
 */
public final class ErrorCodes {

    // Private constructor: this is a constants-only utility class.
    private ErrorCodes() {}

    // ─── Document Verification ─────────────────────────────────────────────
    /** One or more submitted documents failed verification. */
    public static final String DOCUMENT_VERIFICATION_FAILED = "DOCUMENT_VERIFICATION_FAILED";

    /** No documents were found for the candidate at all. */
    public static final String NO_DOCUMENTS_FOUND = "NO_DOCUMENTS_FOUND";

    // ─── Policy Compliance ────────────────────────────────────────────────
    /** Candidate has not accepted one or more mandatory policies. */
    public static final String POLICY_NOT_ACCEPTED = "POLICY_NOT_ACCEPTED";

    // ─── Reference Check ─────────────────────────────────────────────────
    /** Reference verification returned a failed or pending status. */
    public static final String REFERENCE_CHECK_FAILED = "REFERENCE_CHECK_FAILED";

    /** No reference check record exists for this candidate. */
    public static final String REFERENCE_NOT_FOUND = "REFERENCE_NOT_FOUND";

    // ─── Employee Creation ────────────────────────────────────────────────
    /** The candidate record was not found in the pre-onboarding data store. */
    public static final String EMPLOYEE_NOT_FOUND = "EMPLOYEE_NOT_FOUND";

    /** Candidate data is present but missing required fields for conversion. */
    public static final String INVALID_EMPLOYEE_DATA = "INVALID_EMPLOYEE_DATA";

    // ─── General ──────────────────────────────────────────────────────────
    /** Unexpected internal failure during the onboarding pipeline. */
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}
