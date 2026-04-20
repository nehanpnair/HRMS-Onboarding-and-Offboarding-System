package model;

/**
 * Core Employee entity.
 *
 * <p>Extended from the original two-field model to include the full set of
 * fields required by:
 * <ul>
 *   <li>{@code EmployeeFactory.createFullEmployee()} — needs email, dept, role, contact, status.</li>
 *   <li>{@code RoleAccessProxy} / {@code PreOnboardingServiceProxy} — needs {@code getRole()}.</li>
 *   <li>The integration interface and data requirements document.</li>
 * </ul>
 *
 * <p><strong>Backward compatibility:</strong> the original constructor
 * {@code Employee(id, name, years)} is retained. The new full constructor
 * is additive only.
 */
public class Employee {

    private String employeeID;
    private String name;
    private String email;
    private String department;
    private String role;
    private String contactInfo;
    private String status;

    // Original constructor — retained for backward compatibility
    public Employee(String employeeID, String name, int yearsOfService) {
        this.employeeID = employeeID;
        this.name       = name;
    }

    /**
     * Full constructor used by {@link factory.EmployeeFactory#createFullEmployee}.
     */
    public Employee(String employeeID, String name, String email,
                    String department, String role,
                    String contactInfo, String status) {
        this.employeeID  = employeeID;
        this.name        = name;
        this.email       = email;
        this.department  = department;
        this.role        = role;
        this.contactInfo = contactInfo;
        this.status      = status;
    }

    // ─── Getters ──────────────────────────────────────────────────────────

    public String getEmployeeID()  { return employeeID;  }
    public String getName()        { return name;        }
    public String getEmail()       { return email;       }
    public String getDepartment()  { return department;  }
    public String getRole()        { return role;        }
    public String getContactInfo() { return contactInfo; }
    public String getStatus()      { return status;      }
    public int getYearsOfService() {
        return 2; // temporary dummy value
    }

    // ─── Setters (mutable fields) ─────────────────────────────────────────

    /** Updated by {@code EmployeeService.assignRole()} after lookup validation. */
    public void setRole(String role)         { this.role = role;         }

    /** Updated when onboarding/offboarding state changes. */
    public void setStatus(String status)     { this.status = status;     }

    public void setDepartment(String dept)   { this.department = dept;   }
    public void setEmail(String email)       { this.email = email;       }
    public void setContactInfo(String info)  { this.contactInfo = info;  }

    @Override
    public String toString() {
        return "Employee{id='" + employeeID + "', name='" + name
                + "', role='" + role + "', status='" + status + "'}";
    }
}
