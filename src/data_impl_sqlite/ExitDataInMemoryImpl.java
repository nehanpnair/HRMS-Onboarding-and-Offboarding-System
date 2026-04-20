package data_impl_sqlite;

import data.IExitData;
import model.ExitRequest;

import java.util.HashMap;
import java.util.Map;

public class ExitDataInMemoryImpl implements IExitData {

    private Map<String, ExitRequest> exitMap = new HashMap<>();

    @Override
    public void createExitRequest(ExitRequest request) {
        exitMap.put(request.getEmployeeID(), request);
    }

    @Override
    public ExitRequest getExitDetails(String employeeID) {
        return exitMap.get(employeeID);
    }

    @Override
    public void updateExitStatus(String employeeID, String status) {
        ExitRequest req = exitMap.get(employeeID);
        if (req != null) {
            System.out.println("Exit status updated: " + status);
        }
    }
}