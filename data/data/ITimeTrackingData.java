package data.data;
import model.model.*;

// ================= TIME TRACKING =================
public interface ITimeTrackingData {
    Attendance getAttendance(String employeeID);
}
