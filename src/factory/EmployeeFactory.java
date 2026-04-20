package factory;

import model.model.Employee;

/**
 * Factory Pattern:
 * Creates Employee objects.
 */
public class EmployeeFactory {

    public static Employee createBasicEmployee(String id, String name) {
        return new Employee(id, name);
    }

    public static Employee createFullEmployee(String id, String name,
                                              String email, String dept,
                                              String role, String contact,
                                              String status) {

        return new Employee(id, name, email, dept, role, contact, status);
    }
}
