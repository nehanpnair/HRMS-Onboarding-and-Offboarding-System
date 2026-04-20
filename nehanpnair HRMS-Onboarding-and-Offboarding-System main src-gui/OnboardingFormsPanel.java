package gui;

import model.model.*;
import service.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Forms/Customization panel.
 */
public final class OnboardingFormsPanel {

    private OnboardingFormsPanel() {}

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

        // Action
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        JButton loadBtn = primaryButton("Load Forms");
        loadBtn.addActionListener(e -> {
            try {
                empService.loadOnboardingForm();
                JOptionPane.showMessageDialog(loadBtn, "Forms loaded from Customization subsystem!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loadBtn, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionPanel.add(loadBtn);
        content.add(actionPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}
