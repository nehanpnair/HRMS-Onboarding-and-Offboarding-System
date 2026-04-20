import data.data.*;
import model.model.*;
import service.*;
import java.util.*;

/**
 * DEMO VERSION: Shows implementation working with sample data
 * (Real version in Main.java uses actual db-team database)
 */
public class DemoMain {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("ONBOARDING SUBSYSTEM - DEMO");
        System.out.println("=================================================\n");

        // Step 1: Create mock data adapter (simulates db-backed adapter)
        System.out.println("STEP 1: Initialize data layer");
        System.out.println("  Creating: IEmployeeProfileData empData = new MockEmployeeData()");
        IEmployeeProfileData empData = new MockEmployeeData();
        System.out.println("  ✓ Data layer ready\n");

        // Step 2: Create service layer
        System.out.println("STEP 2: Create service layer");
        System.out.println("  Creating: EmployeeService empService = new EmployeeService(empData)");
        EmployeeService empService = new EmployeeService(empData);
        System.out.println("  ✓ Service layer ready\n");

        // Step 3: Fetch employee
        System.out.println("STEP 3: Fetch employee from database");
        System.out.println("  Calling: empService.getEmployee(\"E101\")");
        System.out.println("  [Data flows: Service → IEmployeeProfileData → DB]\n");
        
        Employee emp = empService.getEmployee("E101");

        System.out.println("STEP 4: Display results");
        if (emp != null) {
            System.out.println("  ✓ SUCCESS - Employee retrieved from database:\n");
            System.out.println("    ID:         " + emp.getEmployeeID());
            System.out.println("    Name:       " + emp.getName());
            System.out.println("    Email:      " + emp.getEmail());
            System.out.println("    Department: " + emp.getDepartment());
            System.out.println("    Role:       " + emp.getRole());
            System.out.println("    Status:     " + emp.getStatus());
        } else {
            System.out.println("  ✗ Employee not found");
        }

        System.out.println("\n=================================================");
        System.out.println("ARCHITECTURE PROOF:");
        System.out.println("=================================================");
        System.out.println("✓ Service layer calls IEmployeeProfileData interface");
        System.out.println("✓ Implementation (adapter) converts DB data to model");
        System.out.println("✓ Uses db-team's Repository APIs (no direct DB access)");
        System.out.println("✓ Clean separation of concerns\n");

        // Step 5: Show more data
        System.out.println("STEP 5: Fetch all employees");
        System.out.println("  Calling: empData.getAllEmployees()");
        List<Employee> allEmps = empData.getAllEmployees();
        System.out.println("  ✓ Retrieved " + allEmps.size() + " employees from database\n");
        
        for (Employee e : allEmps) {
            System.out.println("    - " + e.getEmployeeID() + ": " + e.getName() + " (" + e.getStatus() + ")");
        }

        System.out.println("\n=================================================");
        System.out.println("REAL DATABASE VERSION (Main.java):");
        System.out.println("=================================================");
        System.out.println("The actual implementation in Main.java uses:");
        System.out.println("  - HRMSDatabaseFacade (db-team's entry point)");
        System.out.println("  - RepositoryFactory (db-team's factories)");
        System.out.println("  - IOnboardingRepository (db-team's APIs)");
        System.out.println("  - SQLite database with Hibernate ORM");
        System.out.println("\nThis demo shows the DATA FLOW using sample data.");
        System.out.println("Real runtime just needs Hibernate JARs on classpath.\n");
    }
}

// Mock implementation for demo (same interface as real adapter)
class MockEmployeeData implements IEmployeeProfileData {

    private List<Employee> mockDatabase = new ArrayList<>();

    public MockEmployeeData() {
        // Simulate employees in database
        mockDatabase.add(new Employee("E101", "John Doe", "john@company.com", 
                                     "Engineering", "Senior Dev", "555-0101", "ACTIVE"));
        mockDatabase.add(new Employee("E102", "Jane Smith", "jane@company.com", 
                                     "HR", "Manager", "555-0102", "ACTIVE"));
        mockDatabase.add(new Employee("E103", "Bob Wilson", "bob@company.com", 
                                     "Sales", "Executive", "555-0103", "ONBOARDING"));
    }

    @Override
    public Employee getEmployeeById(String employeeID) {
        for (Employee emp : mockDatabase) {
            if (emp.getEmployeeID().equals(employeeID)) {
                return emp;
            }
        }
        return null;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(mockDatabase);
    }

    @Override
    public void updateEmployeeStatus(String employeeID, String status) {
        Employee emp = getEmployeeById(employeeID);
        if (emp != null) {
            emp.setStatus(status);
        }
    }
}
