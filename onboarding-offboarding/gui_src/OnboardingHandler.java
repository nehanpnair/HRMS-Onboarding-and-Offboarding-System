package gui;

import model.model.Employee;

public abstract class OnboardingHandler {
    protected OnboardingHandler next;

    public void setNext(OnboardingHandler next) {
        this.next = next;
    }

    public void process(Employee emp) {
        handleRequest(emp);
        if (next != null) {
            next.process(emp);
        }
    }

    protected abstract void handleRequest(Employee emp);
}

class HRApprovalHandler extends OnboardingHandler {
    @Override
    protected void handleRequest(Employee emp) {
        System.out.println("CHAIN OF RESPONSIBILITY: HR approved onboarding for " + emp.getName());
    }
}

class ITProvisioningHandler extends OnboardingHandler {
    @Override
    protected void handleRequest(Employee emp) {
        System.out.println("CHAIN OF RESPONSIBILITY: IT provisioned accounts for " + emp.getName());
    }
}

class ManagerWelcomeHandler extends OnboardingHandler {
    @Override
    protected void handleRequest(Employee emp) {
        System.out.println("CHAIN OF RESPONSIBILITY: Manager sent welcome email to " + emp.getName());
    }
}
