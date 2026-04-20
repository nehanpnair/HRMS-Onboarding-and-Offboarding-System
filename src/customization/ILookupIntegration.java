package customization;

import java.util.List;

/**
 * INTERFACE: ILookupIntegration
 * Provided by: Customization Subsystem (Code Crafters)
 * Consumed by: Employee Onboarding & Offboarding Subsystem
 *
 * PURPOSE:
 *   Allows the Onboarding & Offboarding subsystem to fetch named value
 *   lists (lookups) for use in dropdown fields, stage selectors, and
 *   any form field that requires a controlled set of values.
 *
 * HOW TO USE:
 *   ILookupIntegration lookup = CustomizationFacade.getLookupIntegration();
 *   List<String> stages = lookup.getValues("ONBOARDING_STAGE");
 *   // → ["Pre-Joining", "Orientation", "Probation", "Confirmed"]
 *
 * LOOKUP CODES PRE-SEEDED FOR YOU:
 *   "ONBOARDING_STAGE"   → Pre-Joining, Orientation, Probation, Confirmed
 *   "EMPLOYMENT_TYPE"    → Full-Time, Part-Time, Contract
 *   "DEPARTMENT"         → HR, Finance, Engineering, Sales
 *   "GENDER"             → Male, Female, Non-Binary
 *
 *   If you need additional lookup types, inform the Customization team —
 *   they will add the lookup type via the Lookup Customizer UI.
 *   Do NOT hardcode dropdown values on your side.
 *
 * IMPORTANT CONSTRAINTS:
 *   - You may READ lookup values freely.
 *   - You may NOT create, modify, or delete lookup types or values —
 *     all lookup management is handled by the Customization subsystem.
 *
 * EXCEPTIONS TO HANDLE:
 *   LOOKUP_VALUE_NOT_FOUND   — lookup type does not exist; check the code
 *   EMPTY_FIELD_NAME         — lookup code passed was blank
 */
public interface ILookupIntegration {

    /**
     * Get all valid values for a named lookup type.
     * Use this to populate dropdowns in your onboarding forms.
     *
     * @param lookupCode  the lookup type name in UPPER_CASE
     *                    (e.g. "ONBOARDING_STAGE", "DEPARTMENT")
     * @return list of string values for that lookup
     *
     * Throws: CustomizationException(LOOKUP_VALUE_NOT_FOUND) if code unknown
     * Throws: CustomizationException(EMPTY_FIELD_NAME) if code is blank
     *
     * Example:
     *   getValues("ONBOARDING_STAGE")
     *   → ["Pre-Joining", "Orientation", "Probation", "Confirmed"]
     */
    List<String> getValues(String lookupCode);

    /**
     * Check whether a specific value is valid within a lookup type.
     * Use this to validate user input before saving.
     *
     * @param lookupCode  the lookup type name (e.g. "DEPARTMENT")
     * @param value       the value to check (e.g. "Engineering")
     * @return true if the value exists and is enabled; false otherwise
     */
    boolean isValueValid(String lookupCode, String value);

    /**
     * Get all lookup types available in the Customization subsystem.
     * Useful for debugging or displaying available options.
     *
     * @return list of all lookup type codes (e.g. ["GENDER", "DEPARTMENT", ...])
     */
    List<String> getAllLookupTypes();

    /**
     * Get the full LookupInfo record for a specific lookup type.
     * Includes code, all values, and enabled status.
     *
     * @param lookupCode  the lookup type name
     * @return LookupInfo record
     *
     * Throws: CustomizationException(LOOKUP_VALUE_NOT_FOUND)
     */
    LookupInfo getLookupInfo(String lookupCode);

    // ─── Data Transfer Object ─────────────────────────────────────────────────

    /**
     * Full information about one lookup type.
     */
    class LookupInfo {
        public String       lookupCode;    // e.g. "ONBOARDING_STAGE"
        public List<String> values;        // all valid values
        public boolean      isEnabled;     // if false, treat as unavailable
    }
}
