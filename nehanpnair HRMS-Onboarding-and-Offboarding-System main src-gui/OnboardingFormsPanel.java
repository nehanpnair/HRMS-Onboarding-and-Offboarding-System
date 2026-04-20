package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Forms/Customization panel with progress tracking.
 */
public final class OnboardingFormsPanel {

    private OnboardingFormsPanel() {}

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
        content.add(label("Customization Forms", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY));
        content.add(Box.createVerticalStrut(16));

        // Info
        JPanel infoCard = card();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.add(label("Loading Onboarding Forms", FONT_UI, TEXT_PRIMARY));
        infoCard.add(label("Employee: " + emp.getName(), FONT_SMALL, TEXT_SECONDARY));
        infoCard.add(Box.createVerticalStrut(12));
        infoCard.add(label("Status: Ready", FONT_SMALL, new Color(0x4AC26B)));
        content.add(infoCard);
        content.add(Box.createVerticalStrut(16));

        // Integration source
        JLabel integrationLabel = label("Integration: Customization Subsystem ✔", FONT_SMALL, TEXT_SECONDARY);
        integrationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);        
        // Forms loaded list - from Customization subsystem
        JPanel formsListPanel = card();
        formsListPanel.setLayout(new BoxLayout(formsListPanel, BoxLayout.Y_AXIS));
        formsListPanel.add(label("Onboarding Checklist Fields (from Customization):", FONT_SMALL, TEXT_MUTED));
        formsListPanel.add(Box.createVerticalStrut(6));
        formsListPanel.add(label("• Full Name", FONT_SMALL, TEXT_SECONDARY));
        formsListPanel.add(label("• Date of Joining", FONT_SMALL, TEXT_SECONDARY));
        formsListPanel.add(label("• Department", FONT_SMALL, TEXT_SECONDARY));
        formsListPanel.add(label("• Employment Type", FONT_SMALL, TEXT_SECONDARY));
        formsListPanel.add(label("• Emergency Contact", FONT_SMALL, TEXT_SECONDARY));
        formsListPanel.add(label("• Previous Employer", FONT_SMALL, TEXT_SECONDARY));
        // Status label
        JLabel statusLabel = label("", FONT_SMALL, new Color(0x4AC26B));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (progress.isFormsLoaded()) {
            statusLabel.setText("✔ Forms loaded successfully");
        }

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton loadBtn = primaryButton(progress.isFormsLoaded() ? "Forms Loaded" : "Load Forms");
        loadBtn.setEnabled(!progress.isFormsLoaded());
        loadBtn.addActionListener(e -> {
            try {
                empService.loadOnboardingForm();
                progress.setFormsLoaded(true);
                statusLabel.setText("✔ Forms loaded successfully");
                loadBtn.setEnabled(false);
                loadBtn.setText("Forms Loaded");
                if (onComplete != null) onComplete.run();
            } catch (Exception ex) {
                statusLabel.setForeground(new Color(0xFF6B6B));
                statusLabel.setText("✗ Error: " + ex.getMessage());
            }
        });
        actionPanel.add(loadBtn);
        
        content.add(integrationLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(formsListPanel);
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
