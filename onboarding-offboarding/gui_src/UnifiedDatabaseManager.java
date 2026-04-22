package gui;

import model.model.Candidate;
import model.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnifiedDatabaseManager {

    private static Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    private static void ensureCandidateSchema(Connection conn) throws Exception {
        if (!columnExists(conn, "candidates", "email")) {
            conn.createStatement().execute("ALTER TABLE candidates ADD COLUMN email TEXT");
        }
        if (!columnExists(conn, "candidates", "phone")) {
            conn.createStatement().execute("ALTER TABLE candidates ADD COLUMN phone TEXT");
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE candidates " +
                "SET email = COALESCE(NULLIF(email, ''), contact_info)")) {
            ps.executeUpdate();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws Exception {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void ensureEmployeeSchema(Connection conn) throws Exception {
        if (!columnExists(conn, "employees", "email")) {
            conn.createStatement().execute("ALTER TABLE employees ADD COLUMN email TEXT");
        }
    }

    // ── CANDIDATES ────────────────────────────────────────────────────────

    public static void insertCandidate(Candidate c) {
        String sql = "INSERT INTO candidates (candidate_id, name, contact_info, email, phone, skills, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureCandidateSchema(conn);
            stmt.setString(1, c.getCandidateID());
            stmt.setString(2, c.getName());
            stmt.setString(3, c.getEmail());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, null);
            stmt.setString(6, c.getSkills());
            stmt.setString(7, "CREATED");
            stmt.executeUpdate();
            System.out.println("DB: Inserted candidate " + c.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCandidateStatus(String candidateId, String status) {
        String sql = "UPDATE candidates SET status = ? WHERE candidate_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, candidateId);
            stmt.executeUpdate();
            System.out.println("DB: Candidate " + candidateId + " status updated to " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Candidate> getAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT candidate_id, name, COALESCE(email, contact_info) AS email, skills, status FROM candidates";
        try (Connection conn = connect()) {
            ensureCandidateSchema(conn);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Candidate c = new Candidate(
                            rs.getString("candidate_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("skills"),
                            null,
                            null,
                            rs.getString("status")
                    );
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── EMPLOYEES ─────────────────────────────────────────────────────────

    public static void migrateCandidateToEmployee(Candidate c) {
        String insertSql = "INSERT INTO employees (emp_id, name, email, department, job_role, employment_status) VALUES (?, ?, ?, ?, ?, ?)";
        String deleteSql = "DELETE FROM candidates WHERE candidate_id = ?";

        try (Connection conn = connect()) {
            ensureEmployeeSchema(conn);
            conn.setAutoCommit(false);
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                
                insertStmt.setString(1, c.getCandidateID());
                insertStmt.setString(2, c.getName());
                insertStmt.setString(3, c.getEmail());
                insertStmt.setString(4, "Unassigned");
                insertStmt.setString(5, "Unassigned");
                insertStmt.setString(6, "ONBOARDING_IN_PROGRESS");
                insertStmt.executeUpdate();

                deleteStmt.setString(1, c.getCandidateID());
                deleteStmt.executeUpdate();

                conn.commit();
                System.out.println("DB: Migrated candidate " + c.getName() + " to employees.");
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> getEmployeesByStatus(String status) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT emp_id, name, email, department, job_role, employment_status FROM employees WHERE employment_status = ?";
        try (Connection conn = connect()) {
            ensureEmployeeSchema(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Employee e = new Employee(
                            rs.getString("emp_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("department"),
                            rs.getString("job_role"),
                            null,
                            rs.getString("employment_status")
                    );
                    list.add(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void updateEmployeeStatus(String empId, String status) {
        String sql = "UPDATE employees SET employment_status = ? WHERE emp_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureEmployeeSchema(conn);
            stmt.setString(1, status);
            stmt.setString(2, empId);
            stmt.executeUpdate();
            System.out.println("DB: Employee " + empId + " status updated to " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateEmployeeDetails(String empId, String department, String role) {
        String sql = "UPDATE employees SET department = ?, job_role = ? WHERE emp_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureEmployeeSchema(conn);
            stmt.setString(1, department);
            stmt.setString(2, role);
            stmt.setString(3, empId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void archiveEmployee(String empId) {
        updateEmployeeStatus(empId, "ARCHIVED");
        System.out.println("DB: Employee " + empId + " archived successfully.");
    }
}
