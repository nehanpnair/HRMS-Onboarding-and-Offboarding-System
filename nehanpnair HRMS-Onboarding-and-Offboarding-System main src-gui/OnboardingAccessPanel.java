package gui;

import model.model.*;
import proxy.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Role-Based Access Control panel.
 */
public final class OnboardingAccessPanel {

    private OnboardingAccessPanel() {}

    public static JPanel build(Employee emp, RoleAccessProxy accessControl) {
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

        // Status area
        JLabel statusLabel = label("No action performed", FONT_SMALL, TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton checkBtn = primaryButton("Check Admin Access");
        checkBtn.addActionListener(e -> {
            try {
                accessControl.performAdminAction(emp);
                statusLabel.setText("Admin access evaluated - check console for details");
                statusLabel.setForeground(new Color(0x4AC26B));
            } catch (Exception ex) {
                statusLabel.setText("Access denied: " + ex.getMessage());
                statusLabel.setForeground(new Color(0xFF6B6B));
            }
        });
        actionPanel.add(checkBtn);
        content.add(actionPanel);
        content.add(Box.createVerticalStrut(12));
        content.add(statusLabel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
