# Customization Subsystem — Integration Package
### For: Employee Onboarding & Offboarding Team
### From: Customization Subsystem — Code Crafters (Section D)

---

## What Is This?

This package defines how your subsystem connects to our Customization Subsystem.
We expose three services for your use:

| Service | What it gives you |
|---|---|
| **Workflow Engine** | Trigger and track approval workflows (onboarding, probation, exit clearance) |
| **Lookup Customizer** | Fetch dropdown value lists (ONBOARDING_STAGE, DEPARTMENT, etc.) |
| **Form Designer** | Fetch form definitions (Onboarding Checklist, Exit Clearance Form) |

---

## Files In This Package

| File | What it is |
|---|---|
| `CustomizationFacade.java` | **The only class you instantiate.** Entry point to all three services. |
| `IWorkflowIntegration.java` | Interface for triggering and monitoring workflows |
| `ILookupIntegration.java` | Interface for fetching lookup value lists |
| `IFormIntegration.java` | Interface for fetching form definitions |
| `CustomizationException.java` | Exception class — catch this from all our method calls |
| `MockCustomizationFacade.java` | **Use during development.** Returns real-looking sample data so you can build without waiting for us. |

---

## Quickstart — Development Phase

Add all `.java` files to your project. Then:

```java
// Step 1: Use the mock during development
CustomizationFacade customization = new MockCustomizationFacade();

// Step 2: Trigger a workflow when an employee joins
IWorkflowIntegration wf = customization.getWorkflowIntegration();
int instanceId = wf.triggerWorkflow("Onboarding Approval", employeeId);

// Step 3: Populate a dropdown in your form
ILookupIntegration lk = customization.getLookupIntegration();
List<String> stages = lk.getValues("ONBOARDING_STAGE");
// → ["Pre-Joining", "Orientation", "Probation", "Confirmed"]

// Step 4: Render our form definition in your UI
IFormIntegration fm = customization.getFormIntegration();
IFormIntegration.FormDefinition form = fm.getFormByName("Onboarding Checklist");
for (IFormIntegration.Field field : form.fields) {
    System.out.println(field.fieldName + " | " + field.fieldType + " | required=" + field.required);
}
```

---

## Switching to Live Integration

When both teams are ready to connect:

```java
// Change only this one line:
// FROM:
CustomizationFacade customization = new MockCustomizationFacade();

// TO:
CustomizationFacade customization = CustomizationFacade.getInstance();
```

No other code changes needed.

---

## Handling Errors

All our methods may throw `CustomizationException`. Always wrap calls:

```java
try {
    int instanceId = wf.triggerWorkflow("Onboarding Approval", employeeId);

} catch (CustomizationException e) {
    switch (e.getErrorCode()) {

        case "WORKFLOW_NOT_FOUND":
            // The workflow name doesn't exist — check spelling or ask us
            break;

        case "WORKFLOW_EXECUTION_FAILED":
            // Something went wrong starting the workflow — retry or alert HR
            break;

        case "FORM_NOT_FOUND":
            // Form name/ID is wrong — verify with Customization team
            break;

        case "FIELD_VALIDATION_FAILED":
            // Employee entered invalid data in a field — show them the error
            break;

        case "LOOKUP_VALUE_NOT_FOUND":
            // Lookup code doesn't exist — check UPPER_CASE spelling
            break;

        default:
            // Log: e.getErrorCode(), e.getCategory(), e.getMessage()
            break;
    }
}
```

---

## Important Rules

| Rule | Why |
|---|---|
| Only call `CustomizationFacade` — never our internal classes | We may refactor internals without notice |
| Do NOT write to our workflow/form/lookup data | You are a consumer; all writes go through Customization UI |
| `EMPLOYEE_DATA_READ_ONLY` — we will never edit your employee records | Data ownership stays with your subsystem |
| If you need a new lookup type or form, ask us — we add it in our UI | You don't create these, we do |

---

## Pre-seeded Data Available to You

**Lookup types ready to use:**
- `ONBOARDING_STAGE` → Pre-Joining, Orientation, Probation, Confirmed
- `EMPLOYMENT_TYPE`  → Full-Time, Part-Time, Contract
- `DEPARTMENT`       → HR, Finance, Engineering, Sales
- `GENDER`           → Male, Female, Non-Binary
- `EXIT_TYPE`        → Resignation, Retirement, Termination, Contract End

**Workflow templates ready to trigger:**
- `"Onboarding Approval"` — 3-step: Manager → IT → HR
- `"Probation Review"`    — 2-step: HR Manager → HR Admin
- `"Exit Clearance"`      — 4-step: Manager → IT → Finance → HR
- `"IT Access Setup"`     — 2-step: IT Admin → IT Manager

**Forms ready to fetch:**
- `"Onboarding Checklist"` — 6 fields including Department and Employment Type dropdowns
- `"Exit Clearance Form"`  — 4 fields including Exit Type dropdown

---

## Questions?

Contact the Customization Subsystem team (Code Crafters, Section D).
Refer to `Customization_Interface_Definitions_v1_updated.docx` for DB-side interfaces.
