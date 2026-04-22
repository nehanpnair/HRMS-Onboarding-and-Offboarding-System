package service;

import model.model.PreOnboardingTask;
import model.model.PreOnboardingTask.TaskStatus;
import model.model.Employee;
import data.data.*;
import gui.PreOnboardingProgressState;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Handles pre-onboarding workflow orchestration.
 * 
 * GRASP: Controller → coordinates between UI and data layer
 * SOLID: Single Responsibility → pre-onboarding business logic
 */
public class PreOnboardingService {
    
    private IEmployeeProfileData employeeData;
    private IProgressData progressData;
    private IPreOnboardingData preOnboardingData;
    private PreOnboardingProgressState progressState;
    private String currentEmployeeID;
    
    // Task execution handlers (can be extended with actual service calls)
    private Map<String, TaskExecutor> taskExecutors;
    
    // Listener for UI updates
    private ProgressUpdateListener updateListener;
    
    @FunctionalInterface
    public interface TaskExecutor {
        boolean execute(PreOnboardingTask task);
    }
    
    @FunctionalInterface
    public interface ProgressUpdateListener {
        void onProgressUpdated(String taskID, TaskStatus newStatus);
    }
    
    // Constructor
    public PreOnboardingService(IEmployeeProfileData employeeData,
                                IProgressData progressData,
                                IPreOnboardingData preOnboardingData) {
        this.employeeData = employeeData;
        this.progressData = progressData;
        this.preOnboardingData = preOnboardingData;
        this.taskExecutors = new HashMap<>();
        initializeTaskExecutors();
    }
    
    /**
     * Initialize task execution handlers with backend service calls
     */
    private void initializeTaskExecutors() {
        taskExecutors.put("PRE_1", this::executeSendWelcomeEmail);
        taskExecutors.put("PRE_2", this::executeSharePortalAccess);
        taskExecutors.put("PRE_3", this::executeRequestDocuments);
        taskExecutors.put("PRE_4", this::executeScheduleOrientation);
        taskExecutors.put("PRE_5", this::executePrepareWorkstation);
    }
    
    /**
     * Set the employee for this pre-onboarding session
     */
    public void setEmployee(String employeeID) {
        this.currentEmployeeID = employeeID;
        this.progressState = new PreOnboardingProgressState(employeeID);
        loadProgressFromDB();
    }
    
    /**
     * Load pre-onboarding progress from database
     */
    private void loadProgressFromDB() {
        try {
            // Try to load from DB progress table
            if (progressData != null) {
                // Assuming progress is stored with processType = "PRE_ONBOARDING"
                Object progress = progressData.getProgress(currentEmployeeID, "PRE_ONBOARDING");
                if (progress != null) {
                    System.out.println("Loaded pre-onboarding progress from DB");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading progress from DB: " + e.getMessage());
        }
    }
    
    /**
     * Get current progress state
     */
    public PreOnboardingProgressState getProgressState() {
        return progressState;
    }
    
    /**
     * Execute a specific pre-onboarding task
     */
    public boolean executeTask(String taskID) {
        if (progressState == null) {
            System.err.println("No employee selected for pre-onboarding");
            return false;
        }
        
        PreOnboardingTask task = progressState.getTaskById(taskID);
        if (task == null) {
            System.err.println("Task not found: " + taskID);
            return false;
        }
        
        // Validate sequential workflow
        PreOnboardingTask nextPending = progressState.getNextPendingTask();
        if (nextPending != null && !nextPending.getTaskID().equals(taskID)) {
            System.err.println("Cannot execute task. Please complete previous tasks first.");
            System.err.println("Next task to complete: " + nextPending.getTitle());
            return false;
        }
        
        // Mark as in-progress
        progressState.updateTaskStatus(taskID, TaskStatus.IN_PROGRESS);
        notifyUpdate(taskID, TaskStatus.IN_PROGRESS);
        
        try {
            // Execute task-specific logic
            TaskExecutor executor = taskExecutors.get(taskID);
            boolean success = executor != null ? executor.execute(task) : defaultExecuteTask(task);
            
            if (success) {
                // Mark as completed
                progressState.updateTaskStatus(taskID, TaskStatus.COMPLETED);
                persistToDatabase(taskID, TaskStatus.COMPLETED);
                notifyUpdate(taskID, TaskStatus.COMPLETED);
                
                // If all completed, trigger completion callback
                if (progressState.isAllCompleted()) {
                    onPreOnboardingCompleted();
                }
                
                return true;
            } else {
                // Mark as failed
                progressState.updateTaskStatus(taskID, TaskStatus.FAILED);
                persistToDatabase(taskID, TaskStatus.FAILED);
                notifyUpdate(taskID, TaskStatus.FAILED);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error executing task: " + e.getMessage());
            progressState.updateTaskStatus(taskID, TaskStatus.FAILED);
            notifyUpdate(taskID, TaskStatus.FAILED);
            return false;
        }
    }
    
    /**
     * Task 1: Send Welcome Email
     */
    private boolean executeSendWelcomeEmail(PreOnboardingTask task) {
        try {
            Employee emp = employeeData.getEmployeeById(currentEmployeeID);
            if (emp == null) return false;
            
            // TODO: Integrate with actual notification/email service
            System.out.println("✓ Sending welcome email to: " + emp.getEmail());
            // NotificationService.sendEmail(emp.getEmail(), "Welcome", welcomeTemplate);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Task 2: Share Portal Access
     */
    private boolean executeSharePortalAccess(PreOnboardingTask task) {
        try {
            Employee emp = employeeData.getEmployeeById(currentEmployeeID);
            if (emp == null) return false;
            
            // TODO: Generate temporary portal credentials
            System.out.println("✓ Generating portal credentials for: " + emp.getName());
            // PortalService.generateCredentials(emp);
            // PortalService.sendCredentials(emp.getEmail());
            
            return true;
        } catch (Exception e) {
            System.err.println("Error sharing portal access: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Task 3: Request Document Submission
     */
    private boolean executeRequestDocuments(PreOnboardingTask task) {
        try {
            Employee emp = employeeData.getEmployeeById(currentEmployeeID);
            if (emp == null) return false;
            
            // TODO: Create document request task/notification
            System.out.println("✓ Creating document request for: " + emp.getName());
            // DocumentService.createRequest(emp.getEmployeeID(), requiredDocuments);
            // NotificationService.sendDocumentRequest(emp.getEmail());
            
            return true;
        } catch (Exception e) {
            System.err.println("Error requesting documents: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Task 4: Schedule First Day Orientation
     */
    private boolean executeScheduleOrientation(PreOnboardingTask task) {
        try {
            Employee emp = employeeData.getEmployeeById(currentEmployeeID);
            if (emp == null) return false;
            
            // TODO: Schedule orientation meeting
            System.out.println("✓ Scheduling orientation for: " + emp.getName());
            // CalendarService.scheduleOrientation(emp, LocalDateTime.now().plusDays(7));
            // NotificationService.sendCalendarInvite(emp.getEmail());
            
            return true;
        } catch (Exception e) {
            System.err.println("Error scheduling orientation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Task 5: Prepare Workstation
     */
    private boolean executePrepareWorkstation(PreOnboardingTask task) {
        try {
            Employee emp = employeeData.getEmployeeById(currentEmployeeID);
            if (emp == null) return false;
            
            // TODO: Create IT workstation setup request
            System.out.println("✓ Creating workstation setup for: " + emp.getName());
            // ITService.createWorkstationRequest(emp);
            // NotificationService.notifyIT(emp);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error preparing workstation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Default task executor (for custom tasks)
     */
    private boolean defaultExecuteTask(PreOnboardingTask task) {
        System.out.println("✓ Executing task: " + task.getTitle());
        return true;
    }
    
    /**
     * Persist task completion to database
     */
    private void persistToDatabase(String taskID, TaskStatus status) {
        try {
            // Create progress ID: employeeID_taskID
            String progressID = currentEmployeeID + "_" + taskID;
            
            // Store in database
            if (progressData != null) {
                progressData.updateProgressStatus(progressID, status.name());
                System.out.println("✓ Persisted to DB: " + taskID + " -> " + status);
            }
        } catch (Exception e) {
            System.err.println("Error persisting to database: " + e.getMessage());
        }
    }
    
    /**
     * Called when all pre-onboarding tasks are completed
     */
    private void onPreOnboardingCompleted() {
        try {
            System.out.println("✓✓✓ PRE-ONBOARDING COMPLETED ✓✓✓");
            
            // Update employee status in DB
            if (preOnboardingData != null) {
                preOnboardingData.updateOnboardingStatus(currentEmployeeID, "PRE_ONBOARDING_COMPLETED");
            }
            
            // Notify UI
            notifyUpdate("ALL", TaskStatus.COMPLETED);
            
        } catch (Exception e) {
            System.err.println("Error on completion: " + e.getMessage());
        }
    }
    
    /**
     * Set progress update listener (for UI refresh)
     */
    public void setProgressUpdateListener(ProgressUpdateListener listener) {
        this.updateListener = listener;
    }
    
    /**
     * Notify listener of progress updates
     */
    private void notifyUpdate(String taskID, TaskStatus status) {
        if (updateListener != null) {
            updateListener.onProgressUpdated(taskID, status);
        }
    }
    
    /**
     * Get list of tasks for current employee
     */
    public List<PreOnboardingTask> getTasks() {
        return progressState != null ? progressState.getTasks() : new ArrayList<>();
    }
    
    /**
     * Get specific task
     */
    public PreOnboardingTask getTask(String taskID) {
        return progressState != null ? progressState.getTaskById(taskID) : null;
    }
    
    /**
     * Check if all tasks completed
     */
    public boolean isCompleted() {
        return progressState != null && progressState.isAllCompleted();
    }
    
    /**
     * Get completion percentage
     */
    public int getCompletionPercentage() {
        return progressState != null ? progressState.getCompletionPercentage() : 0;
    }
}
