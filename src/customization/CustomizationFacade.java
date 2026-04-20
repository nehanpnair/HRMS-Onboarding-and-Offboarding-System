package customization;

/**
 * CLASS: CustomizationFacade  (Entry Point for External Subsystems)
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem
 *
 * ═══════════════════════════════════════════════════════════════════
 *  THIS IS THE ONLY CLASS YOU NEED TO INSTANTIATE.
 *  Do not import or call any other class from our subsystem directly.
 * ═══════════════════════════════════════════════════════════════════
 *
 * USAGE (in your subsystem):
 *
 *   // 1. Get the facade (singleton — call once, reuse)
 *   CustomizationFacade customization = CustomizationFacade.getInstance();
 *
 *   // 2. Use Workflow Engine
 *   IWorkflowIntegration wf = customization.getWorkflowIntegration();
 *   int instanceId = wf.triggerWorkflow("Onboarding Approval", employeeId);
 *
 *   // 3. Use Lookup Customizer
 *   ILookupIntegration lk = customization.getLookupIntegration();
 *   List<String> stages = lk.getValues("ONBOARDING_STAGE");
 *
 *   // 4. Use Form Designer
 *   IFormIntegration fm = customization.getFormIntegration();
 *   IFormIntegration.FormDefinition form = fm.getFormByName("Onboarding Checklist");
 *
 * EXCEPTION HANDLING:
 *   All methods may throw CustomizationException.
 *   Always wrap calls in try-catch and handle by errorCode:
 *
 *   try {
 *       wf.triggerWorkflow("Onboarding Approval", empId);
 *   } catch (CustomizationException e) {
 *       if ("WORKFLOW_NOT_FOUND".equals(e.getErrorCode())) { ... }
 *       if ("WORKFLOW_EXECUTION_FAILED".equals(e.getErrorCode())) { ... }
 *   }
 *
 * DESIGN PATTERN:
 *   Facade (GoF Structural) — hides all internal complexity of the
 *   Customization subsystem behind three clean integration interfaces.
 *   Singleton — only one instance exists per JVM run.
 */
public class CustomizationFacade {

    private static CustomizationFacade instance;

    // The Customization team will wire in the real implementations here.
    // Your code only ever sees the interfaces above — never the internals.
    private IWorkflowIntegration workflowIntegration;
    private ILookupIntegration   lookupIntegration;
    private IFormIntegration     formIntegration;

    protected CustomizationFacade() {
        // Instantiated internally by Customization subsystem.
        // Protected to allow subclasses like MockCustomizationFacade.
        // You receive this via getInstance() — do not call new CustomizationFacade().
    }

    /**
     * Get the single shared instance of the facade.
     * Call once at startup; reuse throughout your subsystem.
     */
    public static CustomizationFacade getInstance() {
        if (instance == null) {
            instance = new CustomizationFacade();
            // NOTE: In the real integration build, the Customization team
            // will inject live implementations here. During development,
            // replace with mock stubs (see MockCustomizationFacade below).
        }
        return instance;
    }

    /**
     * Access the Workflow Engine integration.
     * Trigger and monitor onboarding/offboarding approval workflows.
     */
    public IWorkflowIntegration getWorkflowIntegration() {
        return workflowIntegration;
    }

    /**
     * Access the Lookup Customizer integration.
     * Fetch dropdown value lists for your forms and stage selectors.
     */
    public ILookupIntegration getLookupIntegration() {
        return lookupIntegration;
    }

    /**
     * Access the Form Designer integration.
     * Fetch form definitions to render onboarding checklists and exit forms.
     */
    public IFormIntegration getFormIntegration() {
        return formIntegration;
    }
}
