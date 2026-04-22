package onboarding.exception;

/**
 * Custom exception for all Pre-Onboarding pipeline failures.
 *
 * Carries an {@code errorCode} (see {@link ErrorCodes}) alongside the
 * human-readable message so that callers can branch on specific failure
 * reasons without parsing strings.
 *
 * Extends {@link Exception} (checked) to force callers to handle onboarding
 * failures explicitly — consistent with enterprise Java design.
 */
public class OnboardingException extends Exception {

    /** Machine-readable failure identifier. See {@link ErrorCodes}. */
    private final String errorCode;

    /**
     * @param errorCode  One of the constants in {@link ErrorCodes}.
     * @param message    Human-readable description of what failed.
     */
    public OnboardingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Wraps an underlying cause (e.g. threading or data-layer exception).
     *
     * @param errorCode  One of the constants in {@link ErrorCodes}.
     * @param message    Human-readable description of what failed.
     * @param cause      The original exception that triggered this failure.
     */
    public OnboardingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Returns the machine-readable error code.
     *
     * @return error code string matching a constant in {@link ErrorCodes}.
     */
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[OnboardingException | " + errorCode + "] " + getMessage();
    }
}
