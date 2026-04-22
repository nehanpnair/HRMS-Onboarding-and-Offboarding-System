/*This simulates a controller layer invoking our services. */

import service.*;
import model.model.*;
import data.data.*;
import strategy.*;
import proxy.*;
import factory.*;

public class Main {

    public static void main(String[] args) {
        // TEMP DB CONNECTION TEST
        try {
            Class.forName("org.sqlite.JDBC");
            java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:hrms.db");

            System.out.println("DB Connected");

            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            while (rs.next()) {
                System.out.println("Table: " + rs.getString("name"));
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔻 KEEP YOUR EXISTING CODE BELOW (DO NOT DELETE)

        // 🔹 REAL DATA IMPLEMENTATIONS (using DB)
        IEmployeeProfileData empData = new EmployeeProfileDataImpl();
        IUserAccountData accountData = new UserAccountDataImpl();
        IAssetData assetData = new AssetDataImpl();

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
