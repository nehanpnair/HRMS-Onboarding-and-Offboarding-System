package data_impl_sqlite;

import data.ILeaveData;
import model.Leave;

import java.sql.*;

public class LeaveDataSqliteImpl implements ILeaveData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    @Override
    public Leave getLeaveDetails(String empID) {

        String sql = "SELECT balance FROM leave_balances WHERE emp_id = ? LIMIT 1";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Leave((int) rs.getDouble("balance"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Leave(0);
    }
}
