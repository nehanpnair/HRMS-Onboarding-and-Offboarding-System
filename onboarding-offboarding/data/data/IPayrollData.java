package data.data;
import model.model.*;

// ================= PAYROLL =================
public interface IPayrollData {
    Payroll getPayrollByEmployee(String employeeID);
}
