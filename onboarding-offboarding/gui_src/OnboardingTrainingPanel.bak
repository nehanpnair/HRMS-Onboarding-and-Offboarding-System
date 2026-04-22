package gui;

import model.model.*;
import service.*;
import strategy.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Training Assignment panel with progress tracking.
 */
public final class OnboardingTrainingPanel {

    private OnboardingTrainingPanel() {}

    public static JPanel build(Employee emp, TrainingService trainingService, OnboardingProgressState progress, Runnable onComplete) {
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
        content.add(label("Training Assignment", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Assigning Training (Strategy Pattern)", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Strategy selection
        JPanel strategyCard = card();
        strategyCard.setLayout(new BoxLayout(strategyCard, BoxLayout.Y_AXIS));
        strategyCard.add(label("Training Type", FONT_SMALL, TEXT_MUTED));
        strategyCard.add(Box.createVerticalStrut(8));

        JRadioButton mandatoryRadio = new JRadioButton("Mandatory Training");
        mandatoryRadio.setSelected(true);
        mandatoryRadio.setEnabled(!progress.isTrainingAssigned());
        mandatoryRadio.setBackground(BG_CARD);
        mandatoryRadio.setForeground(TEXT_PRIMARY);

        JRadioButton optionalRadio = new JRadioButton("Optional Training");
        optionalRadio.setEnabled(!progress.isTrainingAssigned());
        optionalRadio.setBackground(BG_CARD);
        optionalRadio.setForeground(TEXT_PRIMARY);

        ButtonGroup group = new ButtonGroup();
        group.add(mandatoryRadio);
        group.add(optionalRadio);

        strategyCard.add(mandatoryRadio);
        strategyCard.add(optionalRadio);
        content.add(strategyCard);
        content.add(Box.createVerticalStrut(16));

        // Status label
        JLabel statusLabel = label("", FONT_SMALL, new Color(0x4AC26B));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (progress.isTrainingAssigned()) {
            statusLabel.setText("✔ Training assigned successfully");
        }

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton assignBtn = primaryButton(progress.isTrainingAssigned() ? "Training Assigned" : "Assign Training");
        assignBtn.setEnabled(!progress.isTrainingAssigned());
        assignBtn.addActionListener(e -> {
            try {
                if (mandatoryRadio.isSelected()) {
                    trainingService.setStrategy(new MandatoryTrainingStrategy());
                } else {
                    trainingService.setStrategy(new OptionalTrainingStrategy());
                }
                trainingService.assignTraining(emp);
                progress.setTrainingAssigned(true);
                statusLabel.setText("✔ Training assigned successfully");
                mandatoryRadio.setEnabled(false);
                optionalRadio.setEnabled(false);
                assignBtn.setEnabled(false);
                assignBtn.setText("Training Assigned");
                if (onComplete != null) onComplete.run();
            } catch (Exception ex) {
                statusLabel.setForeground(new Color(0xFF6B6B));
                statusLabel.setText("✗ Error: " + ex.getMessage());
            }
        });
        actionPanel.add(assignBtn);
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
