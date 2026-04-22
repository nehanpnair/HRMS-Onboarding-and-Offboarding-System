package service;

import data.data.IAssetData;
import model.model.Asset;

import java.util.List;

/**
 * Handles asset allocation to employees.
 */
public class AssetService {

    private IAssetData assetData;

    public AssetService(IAssetData assetData) {
        this.assetData = assetData;
    }

    /**
     * Allocate asset to employee
     */
    public void allocateAsset(String empId, String assetType)
            throws AssetAllocationException {

        if (assetType == null || assetType.isEmpty()) {
            throw new AssetAllocationException("Invalid asset type");
        }

        assetData.allocateAsset(empId, assetType);

        System.out.println("Asset allocated: " + assetType);
    }

    /**
     * Get all assets assigned to employee
     */
    public List<Asset> getAssets(String empId) {
        return assetData.getAssetsByEmployee(empId);
    }
}
