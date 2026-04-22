package data;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation for Pre-Onboarding data.
 * Connects to hrms.db — same DB as the rest of the project.
 *
 * Tables used:
 *   candidates         (candidate_id, name, contact_info, phone, position, department, join_date, status)
 *   pre_onboarding_tasks (task_id, candidate_id, task_name, description, category, status, due_date,
 *                         completed_date, sent_date)
 */
public class PreOnboardingDataSqliteImpl {

    private static final String DB_URL = "jdbc:sqlite:hrms.db";

    // ------------------------------------------------------------------ connect
    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(DB_URL);
    }

    // ---------------------------------------------------------- schema bootstrap
    public void ensureTablesExist() {
        String createCandidates =
            "CREATE TABLE IF NOT EXISTS candidates (" +
            "  candidate_id TEXT PRIMARY KEY," +
            "  contact_info TEXT," +
            "  name TEXT NOT NULL," +
            "  resume_path TEXT," +
            "  skills TEXT," +
            "  source TEXT," +
            "  status TEXT DEFAULT 'PENDING'" +
            ")";

        String createTasks =
            "CREATE TABLE IF NOT EXISTS pre_onboarding_tasks (" +
            "  task_id        TEXT PRIMARY KEY," +
            "  candidate_id   TEXT NOT NULL," +
            "  task_name      TEXT NOT NULL," +
            "  description    TEXT," +
            "  category       TEXT," +
            "  status         TEXT DEFAULT 'PENDING'," +
            "  due_date       TEXT," +
            "  completed_date TEXT," +
            "  sent_date      TEXT," +
            "  FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id)" +
            ")";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCandidates);
            stmt.execute(createTasks);
            seedSampleDataIfEmpty(conn);
        } catch (Exception e) {
            System.err.println("Schema bootstrap failed: " + e.getMessage());
        }
    }

    // --------------------------------------------------------- seed sample data
    private void seedSampleDataIfEmpty(Connection conn) throws Exception {
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT COUNT(*) AS cnt FROM candidates");
        rs.next();
        if (rs.getInt("cnt") > 0) return;   // already seeded

        // Sample candidates
        String[][] candidates = {
            {"CAND-001", "aanya.k@example.com", "Aanya Krishnan", "", "", "", "IN_PROGRESS"}
        };

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO candidates VALUES (?,?,?,?,?,?,?)");
        for (String[] c : candidates) {
            for (int i = 0; i < c.length; i++) ps.setString(i + 1, c[i]);
            ps.executeUpdate();
        }
        ps.close();

        // Seed default tasks for each candidate
        for (String[] c : candidates) {
            seedDefaultTasks(conn, c[0]);
        }
    }

    private void seedDefaultTasks(Connection conn, String candidateId) throws Exception {
        String[][] defaults = {
            {"Send Welcome contact_info",             "Welcome contact_info with company overview and what to expect", "Communication"},
            {"Share Pre-boarding Portal Access","Send credentials for employee self-service portal",     "Documentation"},
            {"Request Document Submission",    "Request ID, certificates, and other required documents","Documentation"},
            {"Schedule First Day Orientation", "Set up calendar invite for day 1 orientation session",  "Preparation"},
            {"Prepare Workstation",            "Coordinate with IT for laptop and desk setup",           "Preparation"},
        };

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO pre_onboarding_tasks " +
            "(task_id, candidate_id, task_name, description, category, status, due_date) " +
            "VALUES (?,?,?,?,?,'PENDING',?)");

        LocalDate base = LocalDate.now();
        for (int i = 0; i < defaults.length; i++) {
            String taskId = "TASK-" + candidateId + "-" + (i + 1);
            ps.setString(1, taskId);
            ps.setString(2, candidateId);
            ps.setString(3, defaults[i][0]);
            ps.setString(4, defaults[i][1]);
            ps.setString(5, defaults[i][2]);
            ps.setString(6, base.plusDays((i + 1) * 3L).toString());
            ps.executeUpdate();
        }
        ps.close();
    }

    // ---------------------------------------------------------- candidate CRUD
    public List<String[]> getAllCandidates() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT candidate_id, contact_info, name, resume_path, skills, source, status FROM candidates";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                rs.getString("candidate_id"),
                rs.getString("contact_info"),
                rs.getString("name"),
                rs.getString("resume_path"),
                rs.getString("skills"),
                rs.getString("source"),
                rs.getString("status")
            });
            }
        } catch (Exception e) {
            System.err.println("getAllCandidates error: " + e.getMessage());
        }
        return list;
    }

    public String[] getCandidateById(String candidateId) {
        String sql = "SELECT candidate_id, contact_info, name, resume_path, skills, source, status FROM candidates WHERE candidate_id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, candidateId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("candidate_id"),
                    rs.getString("contact_info"),
                    rs.getString("name"),
                    rs.getString("resume_path"),
                    rs.getString("skills"),
                    rs.getString("source"),
                    rs.getString("status")
                };
            }
        } catch (Exception e) {
            System.err.println("getCandidateById error: " + e.getMessage());
        }
        return null;
    }

    public void addCandidate(String id, String contact_info, String name,
                            String resume_path, String skills, String source) {

        String sql = "INSERT INTO candidates " +
                    "(candidate_id, contact_info, name, resume_path, skills, source, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.setString(2, contact_info);
            ps.setString(3, name);
            ps.setString(4, resume_path);
            ps.setString(5, skills);
            ps.setString(6, source);

            ps.executeUpdate();
            seedDefaultTasks(conn, id);

        } catch (Exception e) {
            System.err.println("addCandidate error: " + e.getMessage());
        }
    }

    public void updateOnboardingStatus(String candidateId, String status) {
        String sql = "UPDATE candidates SET status = ? WHERE candidate_id = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, candidateId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("updateOnboardingStatus error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------ task CRUD
    public List<String[]> getTasksByCandidate(String candidateId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT task_id, candidate_id, task_name, description, category, status, due_date, completed_date " +
                     "FROM pre_onboarding_tasks WHERE candidate_id = ? ORDER BY due_date ASC";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, candidateId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("task_id"),
                    rs.getString("candidate_id"),
                    rs.getString("task_name"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("status"),
                    rs.getString("due_date"),
                    rs.getString("completed_date") == null ? "" : rs.getString("completed_date")
                });
            }
        } catch (Exception e) {
            System.err.println("getTasksByCandidate error: " + e.getMessage());
        }
        return list;
    }

    public void updateTaskStatus(String taskId, String status) {
        String completedDate = status.equals("COMPLETED") ? LocalDate.now().toString() : null;
        String sql = "UPDATE pre_onboarding_tasks SET status = ?, completed_date = ? WHERE task_id = ?";
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

    public void addTask(String taskId, String candidateId, String taskName,
                        String description, String category, String dueDate) {
        String sql = "INSERT INTO pre_onboarding_tasks " +
                     "(task_id, candidate_id, task_name, description, category, status, due_date) " +
                     "VALUES (?,?,?,?,?,'PENDING',?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taskId);
            ps.setString(2, candidateId);
            ps.setString(3, taskName);
            ps.setString(4, description);
            ps.setString(5, category);
            ps.setString(6, dueDate);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("addTask error: " + e.getMessage());
        }
    }

    // --------------------------------------------------------- aggregate stats
    /** Returns [total, pending, inProgress, completed] for a candidate's tasks */
    public int[] getTaskStats(String candidateId) {
        String sql = "SELECT status, COUNT(*) AS cnt FROM pre_onboarding_tasks " +
                     "WHERE candidate_id = ? GROUP BY status";
        int[] stats = new int[4]; // [total, PENDING, IN_PROGRESS, COMPLETED]
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, candidateId);
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

    /** Returns [totalCandidates, pending, inProgress, completed] across all candidates */
    public int[] getOverallStats() {
        String sql = "SELECT status, COUNT(*) AS cnt FROM candidates GROUP BY status";
        int[] stats = new int[4];
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
            System.err.println("getOverallStats error: " + e.getMessage());
        }
        return stats;
    }
}
