package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.*;
import java.util.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Role Assignment panel with progress tracking.
 */
public final class OnboardingRolePanel {

    private OnboardingRolePanel() {}

    public static JPanel build(Employee emp, EmployeeService empService, OnboardingProgressState progress, Runnable onComplete) {
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
        infoCard.add(label("Role to Assign: " + emp.getDepartment(), FONT_BOLD, new Color(0x7C6AF7)));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Status label (updated after success)
        JLabel statusLabel = label("", FONT_SMALL, new Color(0x4AC26B));
        JLabel timestampLabel = label("", FONT_SMALL, new Color(0x8A8A84));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timestampLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (progress.isRoleAssigned()) {
            statusLabel.setText("✔ Role assigned successfully");
            timestampLabel.setText("Assigned at: " + new SimpleDateFormat("hh:mm a").format(new java.util.Date()));
        }

        // Integration source
        JLabel integrationLabel = label("Integration: Customization Subsystem ✔", FONT_SMALL, TEXT_SECONDARY);
        integrationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton assignBtn = primaryButton(progress.isRoleAssigned() ? "Role Assigned" : "Assign Role");
        assignBtn.setEnabled(!progress.isRoleAssigned());
        assignBtn.addActionListener(e -> {
            try {
                empService.assignRole(emp, emp.getDepartment());
                progress.setRoleAssigned(true);
                String timestamp = new SimpleDateFormat("hh:mm a").format(new java.util.Date());
                statusLabel.setText("✔ Role assigned successfully");
                timestampLabel.setText("Assigned at: " + timestamp);
                assignBtn.setEnabled(false);
                assignBtn.setText("Role Assigned");
                if (onComplete != null) onComplete.run();
            } catch (Exception ex) {
                statusLabel.setForeground(new Color(0xFF6B6B));
                statusLabel.setText("✗ Error: " + ex.getMessage());
            }
        });
        actionPanel.add(assignBtn);
        
        content.add(integrationLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(statusLabel);
        content.add(timestampLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(actionPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
