package data_impl_sqlite;

import data.IEmployeeProfileData;
import model.Employee;

import java.sql.*;
import java.util.*;

public class EmployeeProfileDataSqliteImpl implements IEmployeeProfileData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    // ✅ FIXED: includes name
    @Override
    public Employee getEmployeeById(String empID) {

        String sql = "SELECT emp_id, name, years_of_service FROM employees WHERE emp_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getString("emp_id"),
                        rs.getString("name"),
                        rs.getInt("years_of_service")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Employee(empID, "Unknown", 0);
    }

    // ✅ FIXED: includes name
    @Override
    public List<Employee> getAllEmployees() {

        List<Employee> list = new ArrayList<>();

        String sql = "SELECT emp_id, name, years_of_service FROM employees";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Employee(
                        rs.getString("emp_id"),
                        rs.getString("name"),
                        rs.getInt("years_of_service")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ✅ FIXED column name
    @Override
    public void updateEmployeeStatus(String empID, String status) {

        String sql = "UPDATE employees SET employment_status = ? WHERE emp_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, empID);

            stmt.executeUpdate();

            System.out.println("DB: Employee status updated for " + empID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}