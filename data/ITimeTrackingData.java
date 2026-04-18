package data;
import model.*;

// ================= TIME TRACKING =================
public interface ITimeTrackingData {
    Attendance getAttendance(String employeeID);
}