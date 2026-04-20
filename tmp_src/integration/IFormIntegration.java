package integration;

import java.util.List;

/**
 * INTERFACE: IFormIntegration
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem
 *
 * PURPOSE:
 *   Allows the Onboarding & Offboarding subsystem to fetch form
 *   definitions (structure, fields, layout) created in the
 *   Customization Form Designer. Your subsystem renders the form
 *   and collects the employee's responses — you do not store the
 *   form definition itself.
 *
 * HOW TO USE:
 *   IFormIntegration forms = CustomizationFacade.getFormIntegration();
 *   FormDefinition form = forms.getFormByName("Onboarding Checklist");
 *   // → render form.fields in your UI
 *
 * FORMS PRE-CONFIGURED FOR YOUR USE:
 *   "Onboarding Checklist"   — pre-joining document collection
 *   "Exit Clearance Form"    — offboarding sign-off form
 *   (Additional forms can be created by Customization team on request)
 *
 * IMPORTANT CONSTRAINTS:
 *   - You may READ form definitions and field lists freely.
 *   - You may NOT create, modify, or delete forms or fields —
 *     all form management is done in the Customization Form Designer.
 *   - Submitted form data (employee responses) is stored in YOUR
 *     subsystem. We only provide the structure, not the storage.
 *
 * EXCEPTIONS TO HANDLE:
 *   FORM_NOT_FOUND           — no form with that name/ID; check with Customization team
 *   FIELD_VALIDATION_FAILED  — a submitted field value failed type/mandatory check
 */
public interface IFormIntegration {

    /**
     * Get all forms currently available for onboarding/offboarding use.
     * Use this to let HR admins choose which form to attach to a stage.
     *
     * @return list of FormSummary (ID + name + layout, no fields)
     */
    List<FormSummary> getAllForms();

    /**
     * Get the full definition of a form by its ID, including all fields.
     * Use this to render the form in your onboarding UI.
     *
     * @param formId  unique integer ID of the form
     * @return FormDefinition with all fields and layout info
     *
     * Throws: CustomizationException(FORM_NOT_FOUND) if ID does not exist
     */
    FormDefinition getFormById(int formId);

    /**
     * Get the full definition of a form by its name.
     * Convenience method — use when you know the form name, not the ID.
     *
     * @param formName  exact name of the form (e.g. "Onboarding Checklist")
     * @return FormDefinition with all fields and layout info
     *
     * Throws: CustomizationException(FORM_NOT_FOUND)
     */
    FormDefinition getFormByName(String formName);

    /**
     * Validate a single field value against this form's field definition.
     * Call this before saving an employee's form submission.
     *
     * @param formId     ID of the form the field belongs to
     * @param fieldName  name of the field being validated
     * @param value      value the employee entered
     * @return true if valid; false if type mismatch or mandatory and empty
     *
     * Throws: CustomizationException(FIELD_VALIDATION_FAILED) with detail message
     * Throws: CustomizationException(FORM_NOT_FOUND) if formId is wrong
     */
    boolean validateField(int formId, String fieldName, String value);

    // ─── Data Transfer Objects ─────────────────────────────────────────────────

    /**
     * Lightweight summary of a form — used for listing.
     * Does not include field details.
     */
    class FormSummary {
        public int    formId;      // unique integer ID
        public String formName;    // e.g. "Onboarding Checklist"
        public String layoutType;  // "Grid", "Single Page", or "Tabbed"
    }

    /**
     * Full form definition including all fields.
     * Use this to render the form in your UI.
     */
    class FormDefinition {
        public int         formId;
        public String      formName;
        public String      layoutType;
        public List<Field> fields;    // ordered list of fields to render
    }

    /**
     * One field within a form.
     */
    class Field {
        public String  fieldName;   // label to display
        public String  fieldType;   // "Text", "Number", "Date", "Dropdown", "Textarea"
        public boolean required;    // if true, must not be empty on submit
        public String  lookupCode;  // if fieldType is "Dropdown", fetch values from
                                    // ILookupIntegration.getValues(lookupCode)
                                    // null if not a dropdown
    }
}
