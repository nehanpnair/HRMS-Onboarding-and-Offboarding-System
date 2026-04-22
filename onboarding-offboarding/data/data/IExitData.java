package data.data;
import model.model.*;

// ================= EXIT MANAGEMENT =================
public interface IExitData {
    void createExitRequest(ExitRequest request);
    ExitRequest getExitDetails(String employeeID);
    void updateExitStatus(String employeeID, String status);
}
