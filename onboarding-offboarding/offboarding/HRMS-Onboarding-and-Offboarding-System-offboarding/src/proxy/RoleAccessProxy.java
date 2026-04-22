package proxy;

import model.Employee;

/**
 * Proxy Pattern:
 * Controls access based on role.
 */
public class RoleAccessProxy {

    /**
     * Check if employee has admin privileges
     */
    public void performAdminAction(Employee emp) {

        if (emp.getRole() == null) {
            System.out.println("Role not assigned!");
            return;
        }

        if (!emp.getRole().equalsIgnoreCase("ADMIN")) {
            System.out.println("Access denied!");
            return;
        }

        System.out.println("Admin action performed.");
    }
}