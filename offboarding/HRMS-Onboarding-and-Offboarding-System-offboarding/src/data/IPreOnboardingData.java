package data;
import model.*;

// ================= PRE-ONBOARDING =================
public interface IPreOnboardingData {
    Candidate getCandidateById(String candidateID);
    void updateOnboardingStatus(String candidateID, String status);
}