package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Asset Allocation panel.
 */
public final class OnboardingAssetsPanel {

    private OnboardingAssetsPanel() {}

    public static JPanel build(Employee emp, AssetService assetService) {
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
        content.add(label("Asset Allocation", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Allocating Assets", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Asset checklist
        JPanel assetsCard = card();
        assetsCard.setLayout(new BoxLayout(assetsCard, BoxLayout.Y_AXIS));
        assetsCard.add(label("Standard Assets", FONT_SMALL, TEXT_MUTED));
        assetsCard.add(Box.createVerticalStrut(8));

        JCheckBox laptopCheck = new JCheckBox("Laptop");
        laptopCheck.setSelected(true);
        laptopCheck.setBackground(BG_CARD);
        laptopCheck.setForeground(TEXT_PRIMARY);

        JCheckBox phoneCheck = new JCheckBox("Phone");
        phoneCheck.setSelected(true);
        phoneCheck.setBackground(BG_CARD);
        phoneCheck.setForeground(TEXT_PRIMARY);

        assetsCard.add(laptopCheck);
        assetsCard.add(phoneCheck);
        content.add(assetsCard);
        content.add(Box.createVerticalStrut(16));

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton allocateBtn = primaryButton("Allocate Assets");
        allocateBtn.addActionListener(e -> {
            try {
                if (laptopCheck.isSelected()) {
                    assetService.allocateAsset(emp.getEmployeeID(), "Laptop");
                }
                if (phoneCheck.isSelected()) {
                    assetService.allocateAsset(emp.getEmployeeID(), "Phone");
                }
                JOptionPane.showMessageDialog(allocateBtn, "Assets allocated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(allocateBtn, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(allocateBtn);
        content.add(actionPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
