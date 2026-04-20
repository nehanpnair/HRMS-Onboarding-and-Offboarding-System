package data_impl_sqlite;

import data.IClearanceData;
import model.Clearance;

import java.sql.*;

public class ClearanceDataSqliteImpl implements IClearanceData {

    private Connection connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:hrms.db");
    }

    @Override
    public void createSettlement(Clearance c) {

        String sql = "INSERT INTO clearance_settlements " +
                "(clearance_id, emp_id, clearance_status, settlement_amount, settlement_type, notes, asset_return_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getClearanceID());
            stmt.setString(2, c.getEmployeeID());
            stmt.setString(3, c.getClearanceStatus());
            stmt.setDouble(4, c.getSettlementAmount());
            stmt.setString(5, c.getSettlementType());
            stmt.setString(6, c.getNotes());
            stmt.setString(7, c.getAssetReturnStatus());

            stmt.executeUpdate();

            System.out.println("DB: Clearance inserted for " + c.getEmployeeID());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Clearance getSettlement(String employeeID) {

        String sql = "SELECT * FROM clearance_settlements WHERE emp_id = ? LIMIT 1";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Clearance(
                        rs.getString("clearance_id"),
                        rs.getString("emp_id"),
                        rs.getString("clearance_status"),
                        rs.getDouble("settlement_amount"),
                        rs.getString("settlement_type"),
                        rs.getString("notes"),
                        rs.getString("asset_return_status")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

   @Override
    public void updateClearanceStatus(String clearanceID, String status) {

        String sql = "UPDATE clearance_settlements SET clearance_status = ? WHERE clearance_id = ?";

        try (Connection conn = connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, clearanceID);

            stmt.executeUpdate();

            System.out.println("DB: Clearance updated for ID " + clearanceID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}