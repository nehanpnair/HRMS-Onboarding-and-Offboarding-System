package data.data;

import model.model.*;

// ================= PROGRESS TRACKING =================
public interface IProgressData {
    Progress getProgress(String employeeID, String processType);
    void updateProgressStatus(String processID, String status);
}
