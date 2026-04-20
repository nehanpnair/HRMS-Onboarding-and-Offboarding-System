package gui;

import model.model.*;
import proxy.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.*;
import java.util.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Role-Based Access Control panel with progress tracking.
 */
public final class OnboardingAccessPanel {

    private OnboardingAccessPanel() {}

    public static JPanel build(Employee emp, RoleAccessProxy accessControl, OnboardingProgressState progress, Runnable onComplete) {
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
        content.add(label("Role-Based Access Control", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Evaluating Access (Proxy Pattern)", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("Role: " + (emp.getRole() != null ? emp.getRole() : "Not assigned"), FONT_SMALL, TEXT_SECONDARY));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Access details card
        JPanel accessCard = card();
        accessCard.setLayout(new BoxLayout(accessCard, BoxLayout.Y_AXIS));
        accessCard.add(label("Access Information", FONT_SMALL, TEXT_MUTED));
        accessCard.add(Box.createVerticalStrut(8));

        String role = emp.getDepartment() != null ? emp.getDepartment() : "STANDARD";
        JLabel roleLabel = label("Role: " + role, FONT_UI, TEXT_PRIMARY);
        JLabel accessLevelLabel = label("Access Level: USER", FONT_UI, TEXT_PRIMARY);
        JLabel adminAccessLabel = label("Admin Access: —", FONT_UI, TEXT_SECONDARY);
        JLabel accessStatusLabel = label("Status: Pending", FONT_UI, new Color(0xFFC107));

        accessCard.add(roleLabel);
        accessCard.add(accessLevelLabel);
        accessCard.add(adminAccessLabel);
        accessCard.add(accessStatusLabel);
        content.add(accessCard);
        content.add(Box.createVerticalStrut(16));

        // Status area
        JLabel resultLabel = label("", FONT_SMALL, new Color(0x4AC26B));
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (progress.isAccessControlled()) {
            resultLabel.setText("✔ Access control verified");
            accessStatusLabel.setText("Status: Verified");
            accessStatusLabel.setForeground(new Color(0x4AC26B));
        }

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton checkBtn = primaryButton(progress.isAccessControlled() ? "Access Verified" : "Verify Access");
        checkBtn.setEnabled(!progress.isAccessControlled());
        checkBtn.addActionListener(e -> {
            try {
                accessControl.performAdminAction(emp);
                progress.setAccessControlled(true);
                resultLabel.setText("✔ Access control verified");
                accessStatusLabel.setText("Status: Verified");
                accessStatusLabel.setForeground(new Color(0x4AC26B));
                adminAccessLabel.setText("Admin Access: DENIED");
                adminAccessLabel.setForeground(new Color(0xFF6B6B));
                checkBtn.setEnabled(false);
                checkBtn.setText("Access Verified");
                if (onComplete != null) onComplete.run();
            } catch (Exception ex) {
                resultLabel.setForeground(new Color(0xFF6B6B));
                resultLabel.setText("✗ Error: " + ex.getMessage());
                accessStatusLabel.setText("Status: Denied");
                accessStatusLabel.setForeground(new Color(0xFF6B6B));
                adminAccessLabel.setText("Admin Access: DENIED");
                adminAccessLabel.setForeground(new Color(0xFF6B6B));
            }
        });
        actionPanel.add(checkBtn);
        content.add(actionPanel);
        content.add(Box.createVerticalStrut(12));
        content.add(resultLabel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
