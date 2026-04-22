package gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitDB {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hrms.db");
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("DROP TABLE IF EXISTS candidates");
                stmt.execute("DROP TABLE IF EXISTS employees");
                
                String createCandidates = "CREATE TABLE candidates (" +
                        "candidate_id VARCHAR(36) PRIMARY KEY, " +
                        "contact_info VARCHAR(100), " +
                        "email VARCHAR(100) NOT NULL, " +
                        "phone VARCHAR(20), " +
                        "name VARCHAR(100) NOT NULL, " +
                        "skills TEXT, " +
                        "status VARCHAR(50) NOT NULL)";
                stmt.execute(createCandidates);
                
                String createEmployees = "CREATE TABLE employees (" +
                        "emp_id VARCHAR(36) PRIMARY KEY, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "department VARCHAR(100), " +
                        "job_role VARCHAR(100), " +
                        "employment_status VARCHAR(50) NOT NULL, " +
                        "email VARCHAR(100))";
                stmt.execute(createEmployees);
                
                System.out.println("Database initialized successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
