package data;
import model.*;

// ================= PAYROLL =================
public interface IPayrollData {
    Payroll getPayrollByEmployee(String employeeID);
}