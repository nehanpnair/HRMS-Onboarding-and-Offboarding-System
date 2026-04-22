package gui;

import model.model.Employee;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gui.Theme.*;

/**
 * In-memory representation of an employee who is in (or being added to)
 * the offboarding pipeline.
 */
public class EmployeeRecord {

    // ── Core identity ─────────────────────────────────────────────────────────
    public String   empId;
    public String   name;
    public String   role;
    public String   department;
    public String   lastDay;
    public String   exitType;

    // ── Exit-interview fields (collected after the "exit" step completes) ─────
    public String  successorId            = "";
    public String  reason                 = "";
    public String  feedback               = "";
    public int     rating                 = 0;
    public boolean interviewDataCollected = false;

    // ── Clearance fields ──────────────────────────────────────────────────────
    public boolean laptopReturned = false;
    public boolean idCardReturned = false;
    public boolean accessRevoked  = false;
    public boolean emailDisabled  = false;
    public boolean financeCleared = false;

    // ── Document fields ───────────────────────────────────────────────────────
    public boolean docsGenerated = false;

    // ── Pipeline state ────────────────────────────────────────────────────────
    public Map<String, StepState> stepStates = new LinkedHashMap<>();
    /** Each entry: { timestamp, message, colourHex } */
    public List<String[]> log = new ArrayList<>();

    public int    doneCount     = 0;
    public int    awaitCount    = 0;
    public int    errorCount    = 0;
    public String overallStatus = "Idle";
    public Integer workflowInstanceId;

    // ── Constructors ──────────────────────────────────────────────────────────

    public EmployeeRecord(String empId, String name, String role, String department,
                          String lastDay, String exitType) {
        this.empId      = empId;
        this.name       = name;
        this.role       = role;
        this.department = department;
        this.lastDay    = lastDay;
        this.exitType   = exitType;
        for (String k : PipelineConfig.STEP_KEYS) stepStates.put(k, StepState.PENDING);
    }

    /**
     * Build a record from a DB {@link Employee}.
     */
    public static EmployeeRecord fromEmployee(Employee emp) {
        EmployeeRecord r = new EmployeeRecord(
                emp.getEmployeeID(),
                emp.getName(),
                emp.getRole() != null ? emp.getRole() : "—",
                emp.getDepartment() != null ? emp.getDepartment() : "—",
                "Pending", "Resignation");

        // Set to Idle or running depending on your needs. For now, Idle.
        r.overallStatus = "Idle";
        return r;
    }

    // ── Derived helpers ───────────────────────────────────────────────────────

    public String initials() {
        if (name == null || name.isBlank()) return "??";
        String[] parts = name.split(" ");
        if (parts.length >= 2)
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public float completionPct() {
        long done = stepStates.values().stream().filter(s -> s == StepState.DONE).count();
        return (float) done / PipelineConfig.STEP_KEYS.length;
    }

    /** Key of the next step that is not yet DONE, or {@code null} if all done. */
    public String nextPendingKey() {
        for (String k : PipelineConfig.STEP_KEYS)
            if (stepStates.get(k) != StepState.DONE) return k;
        return null;
    }

    public Color statusColor() {
        switch (overallStatus) {
            case "Complete": return GREEN;
            case "Error":    return RED;
            case "Awaiting": return BLUE;
            case "Running":  return AMBER;
            default:         return TEXT_MUTED;
        }
    }
}
