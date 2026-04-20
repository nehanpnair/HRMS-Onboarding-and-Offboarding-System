package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Account Creation panel.
 */
public final class OnboardingAccountPanel {

    private OnboardingAccountPanel() {}

    public static JPanel build(Employee emp, AccountService accountService) {
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
        content.add(label("Account Creation", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Employee info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Creating User Account", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Account details
        String username = emp.getEmployeeID().toLowerCase() + "_user";
        String password = "SecurePass@123";

        JPanel detailsCard = card();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.add(label("Account Credentials", FONT_SMALL, TEXT_MUTED));
        detailsCard.add(Box.createVerticalStrut(8));
        detailsCard.add(label("Username: " + username, FONT_UI, TEXT_PRIMARY));
        detailsCard.add(label("Password: " + password, FONT_UI, TEXT_PRIMARY));
        content.add(detailsCard);
        content.add(Box.createVerticalStrut(16));

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton createBtn = primaryButton("Create Account");
        createBtn.addActionListener(e -> {
            try {
                accountService.createAccount(emp.getEmployeeID(), username, password);
                JOptionPane.showMessageDialog(createBtn, "Account created successfully!\nUsername: " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(createBtn, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(createBtn);
        content.add(actionPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
