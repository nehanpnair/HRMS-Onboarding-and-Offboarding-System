package data;

import java.util.*;
import model.*;

// ================= NOTIFICATIONS =================
public interface INotificationData {
    void sendNotification(Notification notification);
    List<Notification> getNotifications(String employeeID);
    void updateNotificationStatus(String notificationID, String status);
}