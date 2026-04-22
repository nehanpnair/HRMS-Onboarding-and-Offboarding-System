package model;

public class Clearance {

    private String clearanceID;
    private String employeeID;
    private String clearanceStatus;
    private double settlementAmount;
    private String settlementType;
    private String notes;
    private String assetReturnStatus;

    public Clearance(String clearanceID, String employeeID,
                     String clearanceStatus,
                     double settlementAmount,
                     String settlementType,
                     String notes,
                     String assetReturnStatus) {

        this.clearanceID = clearanceID;
        this.employeeID = employeeID;
        this.clearanceStatus = clearanceStatus;
        this.settlementAmount = settlementAmount;
        this.settlementType = settlementType;
        this.notes = notes;
        this.assetReturnStatus = assetReturnStatus;
    }

    public String getClearanceID() { return clearanceID; }
    public String getEmployeeID() { return employeeID; }
    public String getClearanceStatus() { return clearanceStatus; }
    public double getSettlementAmount() { return settlementAmount; }
    public String getSettlementType() { return settlementType; }
    public String getNotes() { return notes; }
    public String getAssetReturnStatus() { return assetReturnStatus; }
}