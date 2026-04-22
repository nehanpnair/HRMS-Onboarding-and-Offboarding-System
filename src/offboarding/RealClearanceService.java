package offboarding;

import data.data.IAssetData;
import data.data.IUserAccountData;
import data.data.IClearanceData;

public class RealClearanceService implements ClearanceManager {

    private IAssetData assetData;
    private IUserAccountData userData;
    private IClearanceData clearanceData;

    public RealClearanceService(IAssetData assetData,
                                IUserAccountData userData,
                                IClearanceData clearanceData) {

        this.assetData = assetData;
        this.userData = userData;
        this.clearanceData = clearanceData;
    }

    @Override
    public void processClearance(String empID) {

        clearanceData.updateClearanceStatus(empID, "COMPLETED");

        System.out.println("Real service: clearance finalized for " + empID);
    }
}