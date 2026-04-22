package data;
import model.*;

// ================= CLEARANCE SETTLEMENT =================
public interface IClearanceData {
    void createSettlement(Clearance clearance);
    Clearance getSettlement(String employeeID);
    void updateClearanceStatus(String clearanceID, String status);
}