package data;

import java.util.*;
import model.*;

// ================= EMPLOYEE PROFILE =================
public interface IEmployeeProfileData {
    Employee getEmployeeById(String employeeID);
    List<Employee> getAllEmployees();
    void updateEmployeeStatus(String employeeID, String status);
}