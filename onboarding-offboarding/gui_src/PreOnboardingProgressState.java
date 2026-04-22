package gui;

import model.model.PreOnboardingTask;
import model.model.PreOnboardingTask.TaskStatus;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Manages pre-onboarding task states - SIMPLIFIED.
 * Only handles the 5 pre-onboarding steps (strict sequential workflow).
 * No onboarding workflow logic.
 * 
 * GRASP: Information Expert → manages pre-onboarding task state
 * SOLID: Single Responsibility → pre-onboarding only
 */
public class PreOnboardingProgressState {
    
    private List<PreOnboardingTask> tasks;
    private String employeeID;
    
    // The 5 pre-onboarding steps (in order)
    private static final String[] STEP_IDS = {"PRE_1", "PRE_2", "PRE_3", "PRE_4", "PRE_5"};
    private static final String[] STEP_TITLES = {
        "Send Welcome Email",
        "Share Pre-boarding Portal Access",
        "Request Document Submission",
        "Schedule First Day Orientation",
        "Prepare Workstation"
    };
    private static final String[] STEP_DESCRIPTIONS = {
        "Welcome email with company overview and what to expect",
        "Send credentials for employee self-service portal documentation",
        "Request ID, certificates, and other required documents documentation",
        "Set up calendar invite for day 1 orientation session preparation",
        "Coordinate with IT for laptop and desk setup preparation"
    };
    
    public PreOnboardingProgressState(String employeeID) {
        this.employeeID = employeeID;
        this.tasks = new ArrayList<>();
        initializeTasks();
    }
    
    // Backward compatibility
    public PreOnboardingProgressState() {
        this("");
    }
    
    /**
     * Initialize the 5 standard pre-onboarding steps
     */
    private void initializeTasks() {
        tasks.clear();
        for (int i = 0; i < STEP_IDS.length; i++) {
            PreOnboardingTask task = new PreOnboardingTask(
                STEP_IDS[i],
                employeeID,
                STEP_TITLES[i],
                STEP_DESCRIPTIONS[i],
                i + 1,
                LocalDateTime.now().plusDays(7)
            );
            tasks.add(task);
        }
    }
    
    /**
     * Get all tasks
     */
    public List<PreOnboardingTask> getTasks() {
        return new ArrayList<>(tasks);
    }
    
    /**
     * Get task by ID
     */
    public PreOnboardingTask getTaskById(String taskID) {
        return tasks.stream()
            .filter(t -> t.getTaskID().equals(taskID))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get the next uncompleted task (first incomplete one)
     */
    public PreOnboardingTask getNextPendingTask() {
        return tasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Update task status with sequential validation
     * Can only work on the next pending task
     */
    public boolean updateTaskStatus(String taskID, TaskStatus newStatus) {
        PreOnboardingTask task = getTaskById(taskID);
        if (task == null) return false;
        
        // Enforce sequence: can only start/complete the next pending task
        if (newStatus != TaskStatus.PENDING) {
            PreOnboardingTask nextPending = getNextPendingTask();
            if (nextPending != null && !nextPending.getTaskID().equals(taskID)) {
                System.err.println("Cannot execute task. Must complete previous steps first.");
                return false;
            }
        }
        
        task.setStatus(newStatus);
        return true;
    }
    
    /**
     * Check if all 5 pre-onboarding tasks are completed
     */
    public boolean isAllCompleted() {
        return tasks.stream()
            .allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);
    }
    
    /**
     * Get completion percentage
     */
    public int getCompletionPercentage() {
        if (tasks.isEmpty()) return 0;
        long completedCount = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
            .count();
        return (int) ((completedCount * 100) / tasks.size());
    }
    
    /**
     * Reset all tasks to pending
     */
    public void reset() {
        tasks.forEach(t -> t.setStatus(TaskStatus.PENDING));
    }
    
    /**
     * Get task count by status
     */
    public int getTaskCountByStatus(TaskStatus status) {
        return (int) tasks.stream()
            .filter(t -> t.getStatus() == status)
            .count();
    }
    
    /**
     * Backward compatibility methods (for legacy code)
     */
    public boolean isWelcomeEmailSent() { 
        PreOnboardingTask task = getTaskById("PRE_1");
        return task != null && task.getStatus() == TaskStatus.COMPLETED;
    }
    
    public boolean isPortalAccessShared() { 
        PreOnboardingTask task = getTaskById("PRE_2");
        return task != null && task.getStatus() == TaskStatus.COMPLETED;
    }
    
    public boolean isDocumentSubmissionRequested() { 
        PreOnboardingTask task = getTaskById("PRE_3");
        return task != null && task.getStatus() == TaskStatus.COMPLETED;
    }
    
    public boolean isOrientationScheduled() { 
        PreOnboardingTask task = getTaskById("PRE_4");
        return task != null && task.getStatus() == TaskStatus.COMPLETED;
    }
    
    public boolean isWorkstationPrepared() { 
        PreOnboardingTask task = getTaskById("PRE_5");
        return task != null && task.getStatus() == TaskStatus.COMPLETED;
    }
}
