package service;

import model.*;
import data.*;
import exception.*;

import java.util.List;

//integration 
import integration.IEmployeeIntegration;

// Customization imports
import customization.CustomizationFacade;
import customization.MockCustomizationFacade;
import customization.IWorkflowIntegration;
import customization.ILookupIntegration;
import customization.IFormIntegration;
import customization.CustomizationException;

/**
 * Handles Employee-related operations.
 * Acts as Controller (GRASP).
 */
public class EmployeeService implements IEmployeeIntegration { 

    private IEmployeeProfileData employeeData;

    // Customization
    private CustomizationFacade customization;
    private IWorkflowIntegration workflow;
    private ILookupIntegration lookup;
    private IFormIntegration form;

    public EmployeeService(IEmployeeProfileData employeeData) {
        this.employeeData = employeeData;

        // 🔥 USE MOCK FOR NOW
        this.customization = new MockCustomizationFacade();

        this.workflow = customization.getWorkflowIntegration();
        this.lookup = customization.getLookupIntegration();
        this.form = customization.getFormIntegration();
    }

    /**
     * Fetch employee (comes from pre-onboarding)
     */
    public Employee getEmployee(String empId) {
    Employee emp = employeeData.getEmployeeById(empId);

    if (emp == null) {
        System.out.println("Employee not found");
        return null;
    }

    return emp;
    }

    /**
     * Assign role using lookup (NOT HARDCODING HERE - using Customization subsystem for dynamic values) (Customisation subsytem - Lookup Integration)
     */
    public void assignRole(Employee emp, String role) {

    try {
        // Fetch valid roles from customization system
        List<String> roles = lookup.getValues("DEPARTMENT");

        // Validate role against fetched values
        if (!roles.contains(role)) {
            throw new Exception("Invalid role selected");
        }

        emp.setRole(role);

        System.out.println("Role assigned: " + role);

    } catch (CustomizationException e) {
        System.out.println("Lookup error: " + e.getErrorCode());
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}

    /**
     * Trigger onboarding workflow (Customisation subsytem - Workflow Integration)
     */
    public int startOnboarding(Employee emp) {
        try {
            int workflowId = workflow.triggerWorkflow(
                "Onboarding Approval",
                emp.getEmployeeID()
            );

            System.out.println("Workflow started: " + workflowId);
            return workflowId;

        } catch (CustomizationException e) {

            switch (e.getErrorCode()) {

                case "WORKFLOW_NOT_FOUND":
                    System.out.println("Workflow missing!");
                    break;

                case "WORKFLOW_EXECUTION_FAILED":
                    System.out.println("Workflow failed!");
                    break;

                default:
                    System.out.println(e.getMessage());
            }

            return -1;
        }
    }

    /**
     * Fetch onboarding form (Customisation subsytem - Form Integration)
     */
    public void loadOnboardingForm() {
        try {
            IFormIntegration.FormDefinition formDef =
                form.getFormByName("Onboarding Checklist");

            System.out.println("Form: " + formDef.formName);

        } catch (CustomizationException e) {
            System.out.println("Form error: " + e.getErrorCode());
        }
    }
}
