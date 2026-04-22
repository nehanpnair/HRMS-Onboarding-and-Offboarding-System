package model.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single pre-onboarding task with dynamic status tracking.
 * 
 * GRASP: Information Expert → owns task state and logic.
 */
public class PreOnboardingTask {
    
    public enum TaskStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        FAILED("Failed");
        
        private final String displayName;
        
        TaskStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String taskID;
    private String employeeID;
    private String title;
    private String description;
    private TaskStatus status;
    private int sequenceOrder;
    private LocalDateTime dueDate;
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
    private String assignedTo; // Team member responsible
    
    // Constructor
    public PreOnboardingTask(String taskID, String employeeID, String title, 
                             String description, int sequenceOrder, LocalDateTime dueDate) {
        this.taskID = taskID;
        this.employeeID = employeeID;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.PENDING;
        this.sequenceOrder = sequenceOrder;
        this.dueDate = dueDate;
        this.createdDate = LocalDateTime.now();
        this.completedDate = null;
    }
    
    // Getters
    public String getTaskID() { return taskID; }
    public String getEmployeeID() { return employeeID; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public int getSequenceOrder() { return sequenceOrder; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public LocalDateTime getCompletedDate() { return completedDate; }
    public String getAssignedTo() { return assignedTo; }
    
    // Setters with business logic
    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.COMPLETED) {
            this.completedDate = LocalDateTime.now();
        }
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    // Check if task can be completed (sequential enforcement)
    public boolean canComplete() {
        return status == TaskStatus.PENDING || status == TaskStatus.IN_PROGRESS;
    }
    
    // Format completed date for display
    public String getCompletedDateFormatted() {
        if (completedDate == null) return "Not completed";
        return completedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    
    // Check if task is overdue
    public boolean isOverdue() {
        return status != TaskStatus.COMPLETED && dueDate.isBefore(LocalDateTime.now());
    }
}
