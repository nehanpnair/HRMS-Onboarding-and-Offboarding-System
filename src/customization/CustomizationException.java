package customization;

/**
 * CLASS: CustomizationException
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem
 *
 * All methods in IWorkflowIntegration, ILookupIntegration, and
 * IFormIntegration may throw this exception. Always catch it and
 * inspect getErrorCode() to decide how to respond.
 *
 * ─── Error Codes You May Encounter ───────────────────────────────────────────
 *
 * MAJOR (category = "MAJOR") — operation could not complete, must handle:
 *   WORKFLOW_NOT_FOUND         - workflow name/ID does not exist
 *   WORKFLOW_EXECUTION_FAILED  - workflow could not be started (retry or alert HR)
 *   FORM_NOT_FOUND             - form name/ID does not exist
 *   FIELD_VALIDATION_FAILED    - a field value failed type or mandatory check
 *   TASK_SEQUENCE_ERROR        - task flow sequence is invalid
 *
 * MINOR (category = "MINOR") — operation skipped, can often ignore:
 *   LOOKUP_VALUE_NOT_FOUND     - lookup type code does not exist
 *   EMPLOYEE_DATA_READ_ONLY    - you attempted to write employee data via our API
 *                                (you should never hit this if using us correctly)
 *
 * WARNING (category = "WARNING") — informational, log and continue:
 *   EMPTY_FIELD_NAME           - a blank name was passed to a method
 *   DUPLICATE_LOOKUP_VALUE     - value already exists (not an error, just a notice)
 *
 * ─── Suggested Handling Pattern ──────────────────────────────────────────────
 *
 *   try {
 *       int instanceId = wf.triggerWorkflow("Onboarding Approval", empId);
 *   } catch (CustomizationException e) {
 *       switch (e.getErrorCode()) {
 *           case "WORKFLOW_NOT_FOUND":
 *               // show user: "Onboarding Approval workflow is not configured yet"
 *               break;
 *           case "WORKFLOW_EXECUTION_FAILED":
 *               // log error, show user: "Could not start workflow, contact HR admin"
 *               break;
 *           case "INVALID_USER":
 *               // validate your employeeId before calling
 *               break;
 *           default:
 *               // log e.getMessage() and e.getCategory() for debugging
 *       }
 *   }
 */
public class CustomizationException extends RuntimeException {

    private final String errorCode;
    private final String category;      // "MAJOR", "MINOR", or "WARNING"
    private final String resolutionHint;

    public CustomizationException(String errorCode, String category,
                                  String message, String resolutionHint) {
        super(message);
        this.errorCode      = errorCode;
        this.category       = category;
        this.resolutionHint = resolutionHint;
    }

    /** e.g. "WORKFLOW_NOT_FOUND", "FORM_NOT_FOUND" — use this in your switch/if */
    public String getErrorCode()      { return errorCode; }

    /** "MAJOR", "MINOR", or "WARNING" */
    public String getCategory()       { return category; }

    /** Human-readable suggestion on how to resolve the issue */
    public String getResolutionHint() { return resolutionHint; }

    @Override
    public String toString() {
        return "[" + category + "/" + errorCode + "] " + getMessage();
    }
}
