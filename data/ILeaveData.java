package data;
import model.*;

// ================= LEAVE MANAGEMENT =================
public interface ILeaveData {
    Leave getLeaveDetails(String employeeID);
}