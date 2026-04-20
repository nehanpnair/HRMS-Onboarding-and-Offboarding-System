package model;

/**
 * Core Employee entity.
 *
 * Extended to include yearsOfService (needed for SettlementService).
 * Maintains backward compatibility with existing constructors.
 */
public class Employee {

    private String employeeID;
    private String name;
    private String email;
    private String department;
    private String role;
    private String contactInfo;
    private String status;

    private int yearsOfService;

    // ─── Constructor 1 (fixed: now actually stores yearsOfService) ───
    public Employee(String employeeID, String name, int yearsOfService) {
        this.employeeID = employeeID;
        this.name = name;
        this.yearsOfService = yearsOfService;
    }

    // ─── Constructor 2 (full constructor, unchanged) ───
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

    // ─── Getters ─────────────────────────────────────────────────────

    public String getEmployeeID()  { return employeeID; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getDepartment()  { return department; }
    public String getRole()        { return role; }
    public String getContactInfo() { return contactInfo; }
    public String getStatus()      { return status; }

    // ✅ NEW getter (used in SettlementService)
    public int getYearsOfService() { return yearsOfService; }

    // ─── Setters ─────────────────────────────────────────────────────

    public void setRole(String role)         { this.role = role; }
    public void setStatus(String status)     { this.status = status; }
    public void setDepartment(String dept)   { this.department = dept; }
    public void setEmail(String email)       { this.email = email; }
    public void setContactInfo(String info)  { this.contactInfo = info; }

    // Optional (only if needed later)
    public void setYearsOfService(int years) { this.yearsOfService = years; }

    // ─── toString ────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "Employee{id='" + employeeID +
               "', name='" + name +
               "', role='" + role +
               "', status='" + status +
               "', yearsOfService=" + yearsOfService + "}";
    }
}