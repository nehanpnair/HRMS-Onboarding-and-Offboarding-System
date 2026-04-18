package data;
import model.*;

// ================= REFERENCE CHECK =================
public interface IReferenceCheckData {

    // Create
    void addReferenceCheck(ReferenceCheck ref);

    // Read
    ReferenceCheck getReferenceByCandidate(String candidateID);

    // Update
    void updateReferenceStatus(String referenceID, String status);
}