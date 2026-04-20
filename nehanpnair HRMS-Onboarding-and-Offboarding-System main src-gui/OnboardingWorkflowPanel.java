package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Workflow Management panel.
 */
public final class OnboardingWorkflowPanel {

    private OnboardingWorkflowPanel() {}

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
        content.add(label("Workflow Management", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Triggering Onboarding Workflow", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Action & Status
        final int[] workflowId = {-1};
        JLabel statusLabel = label("Workflow not triggered", FONT_SMALL, TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton triggerBtn = primaryButton("Start Workflow");
        triggerBtn.addActionListener(e -> {
            try {
                workflowId[0] = empService.startOnboarding(emp);
                if (workflowId[0] > 0) {
                    statusLabel.setText("Workflow Instance ID: " + workflowId[0]);
                    statusLabel.setForeground(new Color(0x4AC26B));
                    JOptionPane.showMessageDialog(triggerBtn, "Workflow started!\nInstance ID: " + workflowId[0], "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setForeground(new Color(0xFF6B6B));
                JOptionPane.showMessageDialog(triggerBtn, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(triggerBtn);
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
