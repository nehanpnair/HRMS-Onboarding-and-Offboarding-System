package gui;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OffboardingDataSqliteImpl {
    private static final String DB_URL = "jdbc:sqlite:hrms.db";

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(DB_URL);
    }

    public void ensureTablesExist() {
        String createTasks =
            "CREATE TABLE IF NOT EXISTS offboarding_tasks (" +
            "  task_id        TEXT PRIMARY KEY," +
            "  emp_id         TEXT NOT NULL," +
            "  task_name      TEXT NOT NULL," +
            "  description    TEXT," +
            "  category       TEXT," +
            "  status         TEXT DEFAULT 'PENDING'," +
            "  due_date       TEXT," +
            "  completed_date TEXT," +
            "  FOREIGN KEY (emp_id) REFERENCES employees(emp_id)" +
            ")";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTasks);
        } catch (Exception e) {
            System.err.println("Offboarding schema bootstrap failed: " + e.getMessage());
        }
    }

    public void seedTasksForEmployee(String empId) {
        String[][] defaults = {
            {"Exit Interview", "Record exit feedback and reasons", "HR"},
            {"Asset Clearance", "Verify return of laptop, ID card, etc.", "IT"},
            {"Knowledge Transfer", "Upload handover documents and notes", "Operations"},
            {"Final Settlement", "Calculate and process final dues", "Finance"},
            {"Document Generation", "Generate experience certificate and relieving documents", "HR"},
        };

        try (Connection conn = connect();
             PreparedStatement existsStmt = conn.prepareStatement(
                 "SELECT COUNT(*) AS cnt FROM offboarding_tasks WHERE emp_id = ? AND task_name = ?"
             );
             PreparedStatement insertStmt = conn.prepareStatement(
                 "INSERT INTO offboarding_tasks " +
                 "(task_id, emp_id, task_name, description, category, status, due_date) " +
                 "VALUES (?,?,?,?,?,'PENDING',?)"
             )) {

            LocalDate base = LocalDate.now();
            for (int i = 0; i < defaults.length; i++) {
                existsStmt.setString(1, empId);
                existsStmt.setString(2, defaults[i][0]);
                ResultSet rs = existsStmt.executeQuery();
                boolean exists = rs.next() && rs.getInt("cnt") > 0;
                rs.close();

                if (exists) {
                    continue;
                }

                String taskId = "OFF-" + empId + "-" + (i + 1);
                insertStmt.setString(1, taskId);
                insertStmt.setString(2, empId);
                insertStmt.setString(3, defaults[i][0]);
                insertStmt.setString(4, defaults[i][1]);
                insertStmt.setString(5, defaults[i][2]);
                insertStmt.setString(6, base.plusDays((i + 1) * 3L).toString());
                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("seedTasksForEmployee error: " + e.getMessage());
        }
    }

    public List<String[]> getTasksByEmployee(String empId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT task_id, emp_id, task_name, description, category, status, due_date, completed_date " +
                     "FROM offboarding_tasks WHERE emp_id = ? ORDER BY due_date ASC";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("task_id"),
                    rs.getString("emp_id"),
                    rs.getString("task_name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("status"),
                    rs.getString("due_date"),
                    rs.getString("completed_date") == null ? "" : rs.getString("completed_date")
                });
            }
        } catch (Exception e) {
            System.err.println("getTasksByEmployee error: " + e.getMessage());
        }
        return list;
    }

    public void updateTaskStatus(String taskId, String status) {
        String completedDate = status.equals("COMPLETED") ? LocalDate.now().toString() : null;
        String sql = "UPDATE offboarding_tasks SET status = ?, completed_date = ? WHERE task_id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, completedDate);
            ps.setString(3, taskId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("updateTaskStatus error: " + e.getMessage());
        }
    }

    public void updateTaskStatusByName(String empId, String taskName, String status) {
        String completedDate = status.equals("COMPLETED") ? LocalDate.now().toString() : null;
        String sql = "UPDATE offboarding_tasks SET status = ?, completed_date = ? WHERE emp_id = ? AND task_name = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, completedDate);
            ps.setString(3, empId);
            ps.setString(4, taskName);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("updateTaskStatusByName error: " + e.getMessage());
        }
    }

    public int[] getTaskStats(String empId) {
        String sql = "SELECT status, COUNT(*) AS cnt FROM offboarding_tasks " +
                     "WHERE emp_id = ? GROUP BY status";
        int[] stats = new int[4]; // [total, PENDING, IN_PROGRESS, COMPLETED]
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cnt = rs.getInt("cnt");
                stats[0] += cnt;
                switch (rs.getString("status")) {
                    case "PENDING"     -> stats[1] += cnt;
                    case "IN_PROGRESS" -> stats[2] += cnt;
                    case "COMPLETED"   -> stats[3] += cnt;
                }
            }
        } catch (Exception e) {
            System.err.println("getTaskStats error: " + e.getMessage());
        }
        return stats;
    }
}
