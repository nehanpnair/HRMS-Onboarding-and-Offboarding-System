package data;

import java.util.*;
import model.*;

// ================= ASSET ALLOCATION =================
public interface IAssetData {
    void allocateAsset(String employeeID, String assetType);
    void updateAllocationStatus(String assetID, String status);
    List<Asset> getAssetsByEmployee(String employeeID);
}