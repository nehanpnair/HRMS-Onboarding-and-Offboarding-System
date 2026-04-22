package model.model;

public class ExitRequest {

    private String employeeID;
    private String exitType;

    public ExitRequest(String employeeID, String exitType) {
        this.employeeID = employeeID;
        this.exitType = exitType;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getExitType() {
        return exitType;
    }
}
