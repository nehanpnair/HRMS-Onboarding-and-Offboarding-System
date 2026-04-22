package model.model;

public class Employee {

    private String employeeID;
    private String name;
    private String email;
    private String department;
    private String role;
    private String contactInfo;
    private String status;
    private int yearsOfService;

    public Employee(String employeeID, String name) {
        this.employeeID = employeeID;
        this.name = name;
        this.status = "ONBOARDING";
    }

    public Employee(String employeeID, String name, int yearsOfService) {
        this.employeeID = employeeID;
        this.name = name;
        this.yearsOfService = yearsOfService;
    }

    public Employee(String employeeID, String name, String email,
                    String department, String role,
                    String contactInfo, String status) {
        this.employeeID = employeeID;
        this.name = name;
        this.email = email;
        this.department = department;
        this.role = role;
        this.contactInfo = contactInfo;
        this.status = status;
    }

    public String getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public String getContactInfo() { return contactInfo; }
    public String getStatus() { return status; }
    public int getYearsOfService() { return yearsOfService; }

    public void setDepartment(String department) { this.department = department; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
}
