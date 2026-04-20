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
 * Training Assignment panel.
 */
public final class OnboardingTrainingPanel {

    private OnboardingTrainingPanel() {}

    public static JPanel build(Employee emp, TrainingService trainingService) {
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
        mandatoryRadio.setBackground(BG_CARD);
        mandatoryRadio.setForeground(TEXT_PRIMARY);

        JRadioButton optionalRadio = new JRadioButton("Optional Training");
        optionalRadio.setBackground(BG_CARD);
        optionalRadio.setForeground(TEXT_PRIMARY);

        ButtonGroup group = new ButtonGroup();
        group.add(mandatoryRadio);
        group.add(optionalRadio);

        strategyCard.add(mandatoryRadio);
        strategyCard.add(optionalRadio);
        content.add(strategyCard);
        content.add(Box.createVerticalStrut(16));

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton assignBtn = primaryButton("Assign Training");
        assignBtn.addActionListener(e -> {
            try {
                if (mandatoryRadio.isSelected()) {
                    trainingService.setStrategy(new MandatoryTrainingStrategy());
                } else {
                    trainingService.setStrategy(new OptionalTrainingStrategy());
                }
                trainingService.assignTraining(emp);
                JOptionPane.showMessageDialog(assignBtn, "Training assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
