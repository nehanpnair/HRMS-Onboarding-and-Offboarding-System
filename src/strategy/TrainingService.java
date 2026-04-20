package strategy;

import model.model.Employee;
import strategy.TrainingStrategy;

/**
 * Handles training assignment using Strategy Pattern.
 */
public class TrainingService {

    private TrainingStrategy strategy;

    public void setStrategy(TrainingStrategy strategy) {
        this.strategy = strategy;
    }

    public void assignTraining(Employee emp) {
        if (strategy == null) {
            System.out.println("No training strategy set!");
            return;
        }

        strategy.assignTraining(emp);
    }
}
