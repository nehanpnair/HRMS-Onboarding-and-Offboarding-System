package offboarding;

import data_impl_sqlite.EmployeeProfileDataSqliteImpl;
import data_impl_sqlite.LeaveDataSqliteImpl;
import data_impl_sqlite.PayrollDataSqliteImpl;
import data_impl_sqlite.TimeTrackingDataSqliteImpl;

public class DbTest {
    public static void main(String[] args) {

        String empID = "SMOKE_EMP_001";

        // 🔹 Create DB-backed dependencies
        var payrollData = new PayrollDataSqliteImpl();
        var leaveData = new LeaveDataSqliteImpl();
        var attendanceData = new TimeTrackingDataSqliteImpl();
        var employeeData = new EmployeeProfileDataSqliteImpl();

        // 🔹 Settlement service
        SettlementService settlement = new SettlementService(
                payrollData,
                leaveData,
                attendanceData,
                null, // assetData not used
                employeeData
        );

        // 🔹 Test different exit types
        System.out.println("----- RESIGNATION -----");
        double res1 = settlement.calculateSettlement(empID, ExitType.RESIGNATION);
        System.out.println("Final Settlement: " + res1);

        System.out.println("\n----- LAYOFF -----");
        double res2 = settlement.calculateSettlement(empID, ExitType.LAYOFF);
        System.out.println("Final Settlement: " + res2);

        System.out.println("\n----- VRS -----");
        double res3 = settlement.calculateSettlement(empID, ExitType.VRS);
        System.out.println("Final Settlement: " + res3);

        System.out.println("\n----- TERMINATION -----");
        double res4 = settlement.calculateSettlement(empID, ExitType.TERMINATION);
        System.out.println("Final Settlement: " + res4);
    }
}