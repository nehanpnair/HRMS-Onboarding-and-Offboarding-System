package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Role Assignment panel.
 */
public final class OnboardingRolePanel {

    private OnboardingRolePanel() {}

    public static JPanel build(Employee emp, EmployeeService empService) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        if (emp == null) {
            panel.add(label("Select an employee first", FONT_UI, TEXT_MUTED), BorderLayout.CENTER);
            return panel;
        }

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        // Title
        content.add(label("Role Assignment", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Employee: " + emp.getName(), FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("Department: " + emp.getDepartment(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(Box.createVerticalStrut(12));
        infoCard.add(label("Current Role: " + (emp.getRole() != null ? emp.getRole() : "—"), FONT_BOLD, new Color(0x7C6AF7)));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton assignBtn = primaryButton("Assign Role");
        assignBtn.addActionListener(e -> {
            try {
                empService.assignRole(emp, emp.getDepartment());
                JOptionPane.showMessageDialog(assignBtn, "Role assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(assignBtn, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(assignBtn);
        content.add(actionPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
