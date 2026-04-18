package data;
import model.*;

// ================= EXIT MANAGEMENT =================
public interface IExitData {
    void createExitRequest(ExitRequest request);
    ExitRequest getExitDetails(String employeeID);
    void updateExitStatus(String employeeID, String status);
}