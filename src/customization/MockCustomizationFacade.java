package customization;

import java.util.*;

/**
 * CLASS: MockCustomizationFacade
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem — DURING DEVELOPMENT ONLY
 *
 * USE THIS while the Customization backend is not yet connected to your system.
 * Returns realistic sample data so your team can build and test your UI
 * without waiting for us.
 *
 * HOW TO USE:
 *   Instead of:  CustomizationFacade.getInstance()
 *   Use:         new MockCustomizationFacade()
 *
 *   When real integration is ready, swap back to CustomizationFacade.getInstance().
 *   No other code changes needed — it implements the same interfaces.
 *
 * ─── Swap guide ─────────────────────────────────────────────────────────────
 *   // Development:
 *   CustomizationFacade c = new MockCustomizationFacade();
 *
 *   // Production (after integration):
 *   CustomizationFacade c = CustomizationFacade.getInstance();
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class MockCustomizationFacade extends CustomizationFacade {

    // ─── Mock Workflow Integration ────────────────────────────────────────────

    public IWorkflowIntegration getWorkflowIntegration() {
        return new IWorkflowIntegration() {

            private final List<WorkflowInfo> templates = Arrays.asList(
                makeWorkflow(1, "Onboarding Approval",  "Active", "HR Admin",    3),
                makeWorkflow(2, "Probation Review",     "Active", "HR Manager",  2),
                makeWorkflow(3, "Exit Clearance",       "Active", "HR Admin",    4),
                makeWorkflow(4, "IT Access Setup",      "Active", "IT Admin",    2)
            );
            private int instanceCounter = 100;

            @Override
            public List<WorkflowInfo> getActiveWorkflows() {
                return templates;
            }

            @Override
            public WorkflowInfo getWorkflowByName(String name) {
                return templates.stream()
                    .filter(w -> w.workflowName.equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new CustomizationException(
                        "WORKFLOW_NOT_FOUND", "MAJOR",
                        "No workflow found with name: " + name,
                        "Check workflow name matches exactly; ask Customization team to verify."));
            }

            @Override
            public int triggerWorkflow(String workflowName, String employeeId) {
                if (workflowName == null || workflowName.isEmpty())
                    throw new CustomizationException("EMPTY_FIELD_NAME", "WARNING",
                        "Workflow name cannot be empty.", "Pass a valid workflow name.");
                // Confirm it exists (throws WORKFLOW_NOT_FOUND if not)
                getWorkflowByName(workflowName);
                System.out.println("[MOCK] Triggered workflow '" + workflowName
                    + "' for employee " + employeeId + " → instanceId=" + instanceCounter);
                return instanceCounter++;
            }

            @Override
            public String getWorkflowStatus(int instanceId) {
                // Mock: always return "Active" for simplicity
                return "Active";
            }

            @Override
            public List<WorkflowStepInfo> getWorkflowSteps(int instanceId) {
                List<WorkflowStepInfo> steps = new ArrayList<>();
                steps.add(makeStep(1, "Manager Sign-off",  "HR Manager", "Approved", 24));
                steps.add(makeStep(2, "IT Access Request", "IT Admin",   "Pending",  12));
                steps.add(makeStep(3, "HR Confirmation",   "HR Admin",   "Pending",  48));
                return steps;
            }

            private WorkflowInfo makeWorkflow(int id, String name, String status,
                                              String assignee, int stepCount) {
                WorkflowInfo w = new WorkflowInfo();
                w.workflowId   = id;
                w.workflowName = name;
                w.currentStatus= status;
                w.assignedTo   = assignee;
                w.stepCount    = stepCount;
                return w;
            }

            private WorkflowStepInfo makeStep(int id, String name, String assignee,
                                              String status, int escalation) {
                WorkflowStepInfo s = new WorkflowStepInfo();
                s.stepId           = id;
                s.stepName         = name;
                s.assignee         = assignee;
                s.status           = status;
                s.escalationHours  = escalation;
                return s;
            }
        };
    }

    // ─── Mock Lookup Integration ──────────────────────────────────────────────

    public ILookupIntegration getLookupIntegration() {
        return new ILookupIntegration() {

            private final Map<String, List<String>> lookups = new LinkedHashMap<String, List<String>>() {{
                put("ONBOARDING_STAGE",  Arrays.asList("Pre-Joining", "Orientation", "Probation", "Confirmed"));
                put("EMPLOYMENT_TYPE",   Arrays.asList("Full-Time", "Part-Time", "Contract"));
                put("DEPARTMENT",        Arrays.asList("HR", "Finance", "Engineering", "Sales"));
                put("GENDER",            Arrays.asList("Male", "Female", "Non-Binary"));
                put("EXIT_TYPE",         Arrays.asList("Resignation", "Retirement", "Termination", "Contract End"));
            }};

            @Override
            public List<String> getValues(String lookupCode) {
                if (lookupCode == null || lookupCode.isEmpty())
                    throw new CustomizationException("EMPTY_FIELD_NAME", "WARNING",
                        "Lookup code cannot be empty.", "Pass a valid UPPER_CASE lookup code.");
                if (!lookups.containsKey(lookupCode))
                    throw new CustomizationException("LOOKUP_VALUE_NOT_FOUND", "MINOR",
                        "Lookup type does not exist: " + lookupCode,
                        "Check the lookup code. Contact Customization team to add it.");
                return lookups.get(lookupCode);
            }

            @Override
            public boolean isValueValid(String lookupCode, String value) {
                try {
                    return getValues(lookupCode).stream()
                        .anyMatch(v -> v.equalsIgnoreCase(value));
                } catch (CustomizationException e) {
                    return false;
                }
            }

            @Override
            public List<String> getAllLookupTypes() {
                return new ArrayList<>(lookups.keySet());
            }

            @Override
            public LookupInfo getLookupInfo(String lookupCode) {
                LookupInfo info = new LookupInfo();
                info.lookupCode = lookupCode;
                info.values     = getValues(lookupCode);
                info.isEnabled  = true;
                return info;
            }
        };
    }

    // ─── Mock Form Integration ────────────────────────────────────────────────

    public IFormIntegration getFormIntegration() {
        return new IFormIntegration() {

            private final List<FormDefinition> forms = Arrays.asList(
                buildForm(1, "Onboarding Checklist", "Tabbed", Arrays.asList(
                    field("Full Name",          "Text",     true,  null),
                    field("Date of Joining",    "Date",     true,  null),
                    field("Department",         "Dropdown", true,  "DEPARTMENT"),
                    field("Employment Type",    "Dropdown", true,  "EMPLOYMENT_TYPE"),
                    field("Emergency Contact",  "Text",     false, null),
                    field("Previous Employer",  "Text",     false, null)
                )),
                buildForm(2, "Exit Clearance Form", "Single Page", Arrays.asList(
                    field("Last Working Day",   "Date",     true,  null),
                    field("Exit Type",          "Dropdown", true,  "EXIT_TYPE"),
                    field("Assets Returned",    "Text",     true,  null),
                    field("Exit Remarks",       "Textarea", false, null)
                ))
            );

            @Override
            public List<FormSummary> getAllForms() {
                List<FormSummary> summaries = new ArrayList<>();
                for (FormDefinition f : forms) {
                    FormSummary s = new FormSummary();
                    s.formId     = f.formId;
                    s.formName   = f.formName;
                    s.layoutType = f.layoutType;
                    summaries.add(s);
                }
                return summaries;
            }

            @Override
            public FormDefinition getFormById(int formId) {
                return forms.stream()
                    .filter(f -> f.formId == formId)
                    .findFirst()
                    .orElseThrow(() -> new CustomizationException(
                        "FORM_NOT_FOUND", "MAJOR",
                        "No form found with ID: " + formId,
                        "Verify formId. Contact Customization team to check Form Designer."));
            }

            @Override
            public FormDefinition getFormByName(String formName) {
                return forms.stream()
                    .filter(f -> f.formName.equalsIgnoreCase(formName))
                    .findFirst()
                    .orElseThrow(() -> new CustomizationException(
                        "FORM_NOT_FOUND", "MAJOR",
                        "No form found with name: " + formName,
                        "Check form name exactly. Contact Customization team to verify."));
            }

            @Override
            public boolean validateField(int formId, String fieldName, String value) {
                FormDefinition form = getFormById(formId);
                for (Field f : form.fields) {
                    if (f.fieldName.equalsIgnoreCase(fieldName)) {
                        if (f.required && (value == null || value.trim().isEmpty()))
                            throw new CustomizationException(
                                "FIELD_VALIDATION_FAILED", "MAJOR",
                                "Field '" + fieldName + "' is mandatory and cannot be empty.",
                                "Ensure the field has a value before submitting.");
                        return true;
                    }
                }
                return true; // field not found in definition — pass through
            }

            private FormDefinition buildForm(int id, String name, String layout,
                                             List<Field> fields) {
                FormDefinition f = new FormDefinition();
                f.formId     = id;
                f.formName   = name;
                f.layoutType = layout;
                f.fields     = fields;
                return f;
            }

            private Field field(String name, String type, boolean required, String lookup) {
                Field f      = new Field();
                f.fieldName  = name;
                f.fieldType  = type;
                f.required   = required;
                f.lookupCode = lookup;
                return f;
            }
        };
    }
}
