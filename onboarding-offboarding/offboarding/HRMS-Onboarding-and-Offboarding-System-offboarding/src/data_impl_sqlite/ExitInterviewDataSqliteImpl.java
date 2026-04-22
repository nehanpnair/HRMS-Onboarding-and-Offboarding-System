package data_impl_sqlite;

import data.IExitInterviewData;
import model.ExitInterview;

import java.sql.*;

public class ExitInterviewDataSqliteImpl implements IExitInterviewData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    //  1. INSERT
    @Override
    public void recordExitInterview(ExitInterview interview) {

        String sql = "INSERT INTO exit_interviews " +
                "(interview_id, emp_id, feedback_text, primary_reason, satisfaction_rating, exit_date) " +
                "VALUES (?, ?, ?, ?, ?, date('now'))";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, interview.getInterviewId());
            stmt.setString(2, interview.getEmpId());
            stmt.setString(3, interview.getFeedback());
            stmt.setString(4, interview.getReason());
            stmt.setInt(5, interview.getRating());

            stmt.executeUpdate();

            System.out.println("DB: Interview saved for " + interview.getEmpId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  2. FETCH
    @Override
    public ExitInterview getInterviewByEmployee(String empID) {

        String sql = "SELECT * FROM exit_interviews WHERE emp_id = ? LIMIT 1";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new ExitInterview(
                        rs.getString("interview_id"),
                        rs.getString("emp_id"),
                        rs.getString("feedback_text"),
                        rs.getString("primary_reason"),
                        rs.getInt("satisfaction_rating")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateInterviewDetails(String empID, String feedback, String reason) {

        String sql = "UPDATE exit_interviews SET feedback_text = ?, primary_reason = ? WHERE emp_id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, feedback);
            stmt.setString(2, reason);
            stmt.setString(3, empID);

            stmt.executeUpdate();

            System.out.println("DB: Interview updated for " + empID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}