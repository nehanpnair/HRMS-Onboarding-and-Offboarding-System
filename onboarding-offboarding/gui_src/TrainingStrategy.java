package gui;

import model.model.Employee;

public interface TrainingStrategy {
    void assignTraining(Employee emp);
}

class MandatoryTrainingStrategy implements TrainingStrategy {
    @Override
    public void assignTraining(Employee emp) {
        System.out.println("STRATEGY PATTERN: Assigned Mandatory Training (Compliance & Safety) to " + emp.getName());
    }
}

class OptionalTrainingStrategy implements TrainingStrategy {
    @Override
    public void assignTraining(Employee emp) {
        System.out.println("STRATEGY PATTERN: Assigned Optional Training (Advanced Skills) to " + emp.getName());
    }
}
