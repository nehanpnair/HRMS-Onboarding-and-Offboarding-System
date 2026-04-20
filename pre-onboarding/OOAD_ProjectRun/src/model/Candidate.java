package model;

/**
 * Represents a job candidate in the Pre-Onboarding phase.
 *
 * <p>Extended from the original to expose {@code getName()} and additional
 * fields that {@code EmployeeCreationHandler} needs during candidate-to-employee
 * conversion. All new fields are optional to maintain backward compatibility.
 */
public class Candidate {

    private String candidateID;
    private String name;
    private String email;       // optional — used during employee creation
    private String jobRole;     // optional — maps to initial employee role
    private String contactInfo; // optional

    /** Original constructor — retained for backward compatibility. */
    public Candidate(String candidateID, String name) {
        this.candidateID = candidateID;
        this.name        = name;
    }

    /** Full constructor for data layer implementations that populate all fields. */
    public Candidate(String candidateID, String name, String email,
                     String jobRole, String contactInfo) {
        this.candidateID = candidateID;
        this.name        = name;
        this.email       = email;
        this.jobRole     = jobRole;
        this.contactInfo = contactInfo;
    }

    // ─── Getters ──────────────────────────────────────────────────────────

    public String getCandidateID() { return candidateID; }
    public String getName()        { return name;        }
    public String getEmail()       { return email;       }
    public String getJobRole()     { return jobRole;     }
    public String getContactInfo() { return contactInfo; }

    @Override
    public String toString() {
        return "Candidate{id='" + candidateID + "', name='" + name + "'}";
    }
}
