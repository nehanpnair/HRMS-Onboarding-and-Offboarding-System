package data.data;

import model.model.Employee;

// DB imports
import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.factory.RepositoryFactory;
import com.hrms.db.repositories.onboarding.IOnboardingRepository;

import java.util.*;

/**
 * Adapter Pattern Implementation
 * 
 * Converts DB entities → model objects
 * 
 * GRASP: Indirection
 * SOLID: Dependency Inversion
 */
public class EmployeeProfileDataImpl implements IEmployeeProfileData {

    private IOnboardingRepository repo;

    public EmployeeProfileDataImpl() {

        // Step 1: Initialize DB facade (Singleton)
        HRMSDatabaseFacade db = HRMSDatabaseFacade.getInstance();
        db.initialize();

        // Step 2: Get repository factory
        RepositoryFactory factory = db.getRepositories();

        // Step 3: Get onboarding repository (YOUR subsystem)
        this.repo = factory.getOnboardingRepository();
    }

    /**
     * Fetch employee from DB and convert to your model
     */
    @Override
    public Employee getEmployeeById(String employeeID) {

        try {
            // Get DB object (package-private, use reflection)
            Object dbEmp = repo.getEmployeeById(employeeID);

            if (dbEmp == null) return null;

            // Convert DB → Model using reflection (IMPORTANT mapping)
            return new Employee(
                (String) getField(dbEmp, "employeeID"),
                (String) getField(dbEmp, "name"),
                (String) getField(dbEmp, "email"),
                (String) getField(dbEmp, "department"),
                (String) getField(dbEmp, "designation"),
                "",
                (String) getField(dbEmp, "employmentStatus")
            );

        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Employee> getAllEmployees() {

        List<Employee> result = new ArrayList<>();

        try {
            List<?> dbList = repo.getAllEmployees();

            for (Object dbEmp : dbList) {
                result.add(new Employee(
                    (String) getField(dbEmp, "employeeID"),
                    (String) getField(dbEmp, "name"),
                    (String) getField(dbEmp, "email"),
                    (String) getField(dbEmp, "department"),
                    (String) getField(dbEmp, "designation"),
                    "",
                    (String) getField(dbEmp, "employmentStatus")
                ));
            }

        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }

        return result;
    }

    // Helper method to access package-private fields using reflection
    private Object getField(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    @Override
    public void updateEmployeeStatus(String employeeID, String status) {

        try {
            repo.updateEmployeeStatus(employeeID, status);
        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }
}
