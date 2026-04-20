package strategy;

import model.model.Employee;

/**
 * Mandatory training strategy.
 */
public class MandatoryTrainingStrategy implements TrainingStrategy {

    @Override
    public void assignTraining(Employee emp) {
        System.out.println("Assigning mandatory training to " + emp.getName());
    }
}
