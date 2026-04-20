package gui;

import model.model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Completion screen shown after all onboarding steps.
 */
public final class OnboardingCompletionPanel {

    private OnboardingCompletionPanel() {}

    public static JPanel build(Employee emp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(48, 48, 48, 48));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);
        content.setOpaque(false);

        // Success icon (text-based)
        JLabel successIcon = label("✓", new Font("Segoe UI", Font.BOLD, 64), ACCENT);
        successIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(successIcon);
        content.add(Box.createVerticalStrut(24));

        // Title
        JLabel title = label("Onboarding Completed Successfully!", new Font("Segoe UI", Font.BOLD, 18), TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(12));

        // Subtitle
        JLabel subtitle = label("Employee is ready to start", FONT_UI, TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(32));

        // Summary card
        JPanel summary = card();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setMaximumSize(new Dimension(500, 300));
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);

        summary.add(label("Employee Summary", new Font("Segoe UI", Font.BOLD, 12), ACCENT));
        summary.add(Box.createVerticalStrut(12));

        summary.add(label("ID: " + emp.getEmployeeID(), FONT_UI, TEXT_PRIMARY));
        summary.add(label("Name: " + emp.getName(), FONT_UI, TEXT_PRIMARY));
        summary.add(label("Department: " + emp.getDepartment(), FONT_UI, TEXT_PRIMARY));
        summary.add(label("Status: " + emp.getStatus(), FONT_UI, new Color(0x4AC26B)));
        summary.add(Box.createVerticalStrut(16));

        summary.add(label("Completed Steps:", new Font("Segoe UI", Font.BOLD, 11), TEXT_SECONDARY));
        summary.add(Box.createVerticalStrut(6));
        summary.add(label("• [X] Role Assigned", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Workflow Started", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Forms Loaded", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Assets Allocated", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Account Created", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Training Assigned", FONT_SMALL, new Color(0x4AC26B)));
        summary.add(label("• [X] Access Control Verified", FONT_SMALL, new Color(0x4AC26B)));

        content.add(summary);
        content.add(Box.createVerticalStrut(32));

        // Message
        JLabel msg = label("The employee can now log in and start their first day.", FONT_SMALL, TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(msg);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
}
