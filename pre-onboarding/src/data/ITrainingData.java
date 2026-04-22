package data;

import java.util.*;
import model.*;

// ================= TRAINING MANAGEMENT =================
public interface ITrainingData {
    void assignTraining(Training training);
    List<Training> getTrainingByEmployee(String employeeID);
    void updateTrainingStatus(String trainingID, String status);
}
