package data_impl_sqlite;

import data.ITimeTrackingData;
import model.Attendance;

import java.sql.*;

public class TimeTrackingDataSqliteImpl implements ITimeTrackingData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    @Override
    public Attendance getAttendance(String empID) {

        String sql = "SELECT working_days_in_month FROM attendance WHERE emp_id = ? LIMIT 1";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Attendance(rs.getInt("working_days_in_month"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Attendance(0);
    }
}