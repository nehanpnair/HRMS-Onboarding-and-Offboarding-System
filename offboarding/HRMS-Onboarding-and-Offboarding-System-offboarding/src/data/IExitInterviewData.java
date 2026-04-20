package data;
import model.*;

// ================= EXIT INTERVIEW =================
public interface IExitInterviewData {

    // Create
    void recordExitInterview(ExitInterview interview);

    // Read
    ExitInterview getInterviewByEmployee(String employeeID);

    // Update
    void updateInterviewDetails(String interviewID, String feedback, String reason);
}