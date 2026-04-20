import java.util.List;

/**
 * INTERFACE: IWorkflowIntegration
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem
 *
 * PURPOSE:
 *   Allows the Onboarding & Offboarding subsystem to trigger, monitor,
 *   and query approval workflows managed by the Customization Subsystem.
 *
 * HOW TO USE:
 *   Do NOT instantiate workflow classes directly.
 *   Use only this interface. The Customization team will provide
 *   the concrete implementation class at integration time.
 *
 *   Example usage in your code:
 *     IWorkflowIntegration wf = CustomizationFacade.getWorkflowIntegration();
 *     int id = wf.triggerWorkflow("Onboarding Approval", employeeId);
 *
 * IMPORTANT CONSTRAINTS:
 *   - You may READ workflow status and definitions freely.
 *   - You may TRIGGER workflows for onboarding/offboarding events.
 *   - You may NOT create, edit, or delete workflow templates — that
 *     is the exclusive responsibility of the Customization subsystem.
 *
 * EXCEPTIONS TO HANDLE (defined in CustomizationException):
 *   WORKFLOW_EXECUTION_FAILED  — workflow could not be started; retry or alert HR
 *   WORKFLOW_NOT_FOUND         — no workflow exists with that name; verify name
 *   INVALID_USER               — employeeId not recognized; validate before calling
 */
public interface IWorkflowIntegration {

    /**
     * Retrieve all workflow templates that are currently active
     * and relevant to the onboarding/offboarding context.
     *
     * @return list of active WorkflowInfo objects
     *
     * Usage: Call on page load to populate workflow selection dropdowns.
     */
    List<WorkflowInfo> getActiveWorkflows();

    /**
     * Retrieve a specific workflow template by its unique name.
     *
     * @param workflowName  exact name of the workflow (e.g. "Onboarding Approval")
     * @return WorkflowInfo for that workflow, or throws WORKFLOW_NOT_FOUND
     *
     * Throws: CustomizationException(WORKFLOW_NOT_FOUND)
     */
    WorkflowInfo getWorkflowByName(String workflowName);

    /**
     * Trigger a workflow instance for a specific employee event.
     * Creates a live running instance of the named workflow template,
     * assigned to the given employee.
     *
     * @param workflowName  name of the workflow template to trigger
     *                      (e.g. "Onboarding Approval", "Exit Clearance")
     * @param employeeId    ID of the employee this workflow runs for
     *                      (must be a valid ID from your subsystem)
     * @return instanceId   unique ID of this running workflow instance;
     *                      store this to check status later
     *
     * Throws: CustomizationException(WORKFLOW_EXECUTION_FAILED)
     * Throws: CustomizationException(WORKFLOW_NOT_FOUND)
     * Throws: CustomizationException(INVALID_USER)
     */
    int triggerWorkflow(String workflowName, String employeeId);

    /**
     * Get the current status of a running workflow instance.
     *
     * @param instanceId  the ID returned by triggerWorkflow()
     * @return status string — one of: "Active", "Completed", "Rejected", "Pending"
     *
     * Throws: CustomizationException(WORKFLOW_NOT_FOUND) if instanceId is invalid
     */
    String getWorkflowStatus(int instanceId);

    /**
     * Get all steps (and their completion state) for a running workflow instance.
     * Useful for rendering a progress tracker on your onboarding dashboard.
     *
     * @param instanceId  the ID returned by triggerWorkflow()
     * @return ordered list of WorkflowStepInfo objects
     */
    List<WorkflowStepInfo> getWorkflowSteps(int instanceId);

    // ─── Data Transfer Objects ────────────────────────────────────────────────

    /**
     * Represents a workflow template definition.
     * Read-only from your subsystem's perspective.
     */
    class WorkflowInfo {
        public int    workflowId;    // unique ID
        public String workflowName;  // e.g. "Onboarding Approval"
        public String currentStatus; // "Active" or "Inactive"
        public String assignedTo;    // default assignee role (e.g. "HR Admin")
        public int    stepCount;     // number of steps in this workflow
    }

    /**
     * Represents one step in a running workflow instance.
     */
    class WorkflowStepInfo {
        public int    stepId;
        public String stepName;          // e.g. "Manager Sign-off"
        public String assignee;          // person/role responsible
        public String status;            // "Pending", "Approved", "Rejected"
        public int    escalationHours;   // auto-escalates after N hours if not actioned
    }
}
