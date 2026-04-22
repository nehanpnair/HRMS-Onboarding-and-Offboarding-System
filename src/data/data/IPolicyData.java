package data.data;

import java.util.*;
import model.model.*;


// ================= POLICY COMPLIANCE =================
public interface IPolicyData {
    List<Policy> getAllPolicies();
    void updateComplianceStatus(String policyID, String status);
}
