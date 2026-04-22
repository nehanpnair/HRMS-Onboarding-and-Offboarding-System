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
 * Workflow Management panel with progress tracking.
 */
public final class OnboardingWorkflowPanel {

    private OnboardingWorkflowPanel() {}

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

        // Integration source
        JLabel integrationLabel = label("Integration: Customization Subsystem ✔", FONT_SMALL, TEXT_SECONDARY);
        integrationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action & Status
        final int[] workflowId = {-1};
        JLabel statusLabel = label("Workflow not triggered", FONT_SMALL, TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (progress.isWorkflowStarted()) {
            statusLabel.setText("✔ Workflow started successfully");
            statusLabel.setForeground(new Color(0x4AC26B));
        }

        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton triggerBtn = primaryButton(progress.isWorkflowStarted() ? "Workflow Started" : "Start Workflow");
        triggerBtn.setEnabled(!progress.isWorkflowStarted());
        triggerBtn.addActionListener(e -> {
            try {
                workflowId[0] = empService.startOnboarding(emp);
                if (workflowId[0] > 0) {
                    progress.setWorkflowStarted(true);
                    statusLabel.setText("✔ Workflow started successfully (ID: " + workflowId[0] + ")");
                    statusLabel.setForeground(new Color(0x4AC26B));
                    triggerBtn.setEnabled(false);
                    triggerBtn.setText("Workflow Started");
                    if (onComplete != null) onComplete.run();
                }
            } catch (Exception ex) {
                statusLabel.setText("✗ Error: " + ex.getMessage());
                statusLabel.setForeground(new Color(0xFF6B6B));
            }
        });
        actionPanel.add(triggerBtn);
        
        content.add(integrationLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(statusLabel);
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
