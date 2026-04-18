/*This simulates a controller layer invoking our services. */

import service.*;
import model.*;
import data.*;
import strategy.*;
import proxy.*;
import factory.*;

public class Main {

    public static void main(String[] args) {

        // 🔹 MOCK DATA IMPLEMENTATIONS (for demo)
        IEmployeeProfileData empData = new IEmployeeProfileData() {
            @Override
            public Employee getEmployeeById(String employeeID) {
                return EmployeeFactory.createBasicEmployee(employeeID, "Niharika");
            }

            @Override
            public java.util.List<Employee> getAllEmployees() {
                return null;
            }

            @Override
            public void updateEmployeeStatus(String employeeID, String status) {}
        };

        IUserAccountData accountData = new IUserAccountData() {
            public UserAccount getUserByUsername(String username) { return null; }
            public void createUserAccount(UserAccount account) {}
            public void updatePassword(String userID, String password) {}
            public void updateAccessStatus(String userID, String status) {}
        };

        IAssetData assetData = new IAssetData() {
            public void allocateAsset(String employeeID, String assetType) {}
            public void updateAllocationStatus(String assetID, String status) {}
            public java.util.List<Asset> getAssetsByEmployee(String employeeID) { return null; }
        };

        // 🔹 SERVICES
        EmployeeService empService = new EmployeeService(empData);
        AccountService accService = new AccountService(accountData);
        AssetService assetService = new AssetService(assetData);
        TrainingService trainingService = new TrainingService();
        RoleAccessProxy proxy = new RoleAccessProxy();

        try {

            // STEP 1: Fetch Employee
            Employee emp = empService.getEmployee("E101");
            System.out.println("Employee: " + emp.getName());

            // STEP 2: Assign Role (using lookup)
            empService.assignRole(emp, "Engineering");

            // STEP 3: Trigger Workflow
            empService.startOnboarding(emp);

            // STEP 4: Load Form
            empService.loadOnboardingForm();

            // STEP 5: Create Account
            accService.createAccount("U101", "test-user", "pass123");

            // STEP 6: Allocate Asset
            assetService.allocateAsset(emp.getEmployeeID(), "Laptop");

            // STEP 7: Assign Training (Strategy Pattern)
            trainingService.setStrategy(new MandatoryTrainingStrategy());
            trainingService.assignTraining(emp);

            // STEP 8: Role-based Access (Proxy Pattern)
            proxy.performAdminAction(emp);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
