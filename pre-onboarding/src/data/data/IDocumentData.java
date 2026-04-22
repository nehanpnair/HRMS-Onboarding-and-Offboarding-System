package data.data;

import java.util.*;
import model.model.*;


// ================= DOCUMENT MANAGEMENT =================
public interface IDocumentData {
    void uploadDocument(Document doc);
    List<Document> getDocumentsByEmployee(String employeeID);
    void updateVerificationStatus(String documentID, String status);
}
