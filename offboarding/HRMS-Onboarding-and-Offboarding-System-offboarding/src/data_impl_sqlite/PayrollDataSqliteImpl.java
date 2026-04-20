package data_impl_sqlite;

import data.IPayrollData;
import model.Payroll;

import java.sql.*;

public class PayrollDataSqliteImpl implements IPayrollData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    @Override
    public Payroll getPayrollByEmployee(String empID) {

        String sql = "SELECT final_net_pay FROM payroll_results WHERE emp_id = ? LIMIT 1";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Payroll(rs.getDouble("final_net_pay"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Payroll(0);
    }
}