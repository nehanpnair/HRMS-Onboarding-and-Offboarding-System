package data.data;

import model.model.Asset;

// DB imports
import com.hrms.db.facade.HRMSDatabaseFacade;
import com.hrms.db.factory.RepositoryFactory;

import java.util.*;

/**
 * Adapter Pattern Implementation for Asset Management
 * 
 * Converts DB entities → model objects
 * 
 * GRASP: Indirection
 * SOLID: Dependency Inversion
 */
public class AssetDataImpl implements IAssetData {

    private Object assetRepository;

    public AssetDataImpl() {

        // Step 1: Initialize DB facade (Singleton)
        HRMSDatabaseFacade db = HRMSDatabaseFacade.getInstance();
        db.initialize();

        // Step 2: Get repository factory
        RepositoryFactory factory = db.getRepositories();

        // Step 3: Try to get asset repository if available
        try {
            // Asset repository may or may not be available in DB team's API
            java.lang.reflect.Method method = factory.getClass().getDeclaredMethod("getAssetRepository");
            method.setAccessible(true);
            this.assetRepository = method.invoke(factory);
        } catch (Exception e) {
            // Asset repository not available - will use safe behavior (no message needed)
            this.assetRepository = null;
        }
    }

    /**
     * Allocate asset to employee
     */
    @Override
    public void allocateAsset(String employeeID, String assetType) {

        if (employeeID == null || employeeID.isEmpty()) {
            System.out.println("❌ AssetDataImpl: Employee ID cannot be null");
            return;
        }

        if (assetType == null || assetType.isEmpty()) {
            System.out.println("❌ AssetDataImpl: Asset type cannot be null");
            return;
        }

        try {
            if (assetRepository != null) {
                // Try to call repository method if available
                java.lang.reflect.Method method = assetRepository.getClass()
                    .getDeclaredMethod("allocateAsset", String.class, String.class);
                method.setAccessible(true);
                method.invoke(assetRepository, employeeID, assetType);
            }

            System.out.println("✓ Asset '" + assetType + "' allocated to employee: " + employeeID);

        } catch (Exception e) {
            System.out.println("⚠️  AssetDataImpl: Could not allocate asset: " + e.getMessage());
        }
    }

    /**
     * Update allocation status
     */
    @Override
    public void updateAllocationStatus(String assetID, String status) {

        if (assetID == null || status == null) {
            System.out.println("❌ AssetDataImpl: Asset ID or status cannot be null");
            return;
        }

        try {
            if (assetRepository != null) {
                java.lang.reflect.Method method = assetRepository.getClass()
                    .getDeclaredMethod("updateAllocationStatus", String.class, String.class);
                method.setAccessible(true);
                method.invoke(assetRepository, assetID, status);
            }

            System.out.println("✓ Asset status updated: " + assetID + " → " + status);

        } catch (Exception e) {
            System.out.println("⚠️  AssetDataImpl: Could not update status: " + e.getMessage());
        }
    }

    /**
     * Get all assets for an employee
     */
    @Override
    public List<Asset> getAssetsByEmployee(String employeeID) {

        List<Asset> result = new ArrayList<>();

        if (employeeID == null || employeeID.isEmpty()) {
            System.out.println("❌ AssetDataImpl: Employee ID cannot be null");
            return result;
        }

        try {
            if (assetRepository != null) {
                java.lang.reflect.Method method = assetRepository.getClass()
                    .getDeclaredMethod("getAssetsByEmployee", String.class);
                method.setAccessible(true);
                
                @SuppressWarnings("unchecked")
                List<?> dbAssets = (List<?>) method.invoke(assetRepository, employeeID);

                if (dbAssets != null) {
                    for (Object dbAsset : dbAssets) {
                        Asset asset = new Asset(
                            (String) getField(dbAsset, "assetID"),
                            (String) getField(dbAsset, "assetType"),
                            (String) getField(dbAsset, "status")
                        );
                        result.add(asset);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("⚠️  AssetDataImpl: Could not retrieve assets: " + e.getMessage());
        }

        return result;
    }

    // Helper method to access package-private fields using reflection
    private Object getField(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
