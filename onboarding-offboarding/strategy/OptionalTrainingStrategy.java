package strategy;

import model.model.Employee;

/**
 * Optional training strategy.
 */
public class OptionalTrainingStrategy implements TrainingStrategy {

    @Override
    public void assignTraining(Employee emp) {
        System.out.println("Assigning optional training to " + emp.getName());
    }
}
