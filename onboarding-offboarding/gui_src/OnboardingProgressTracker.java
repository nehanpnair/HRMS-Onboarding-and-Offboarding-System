package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Visual progress tracker showing onboarding step completion.
 */
public final class OnboardingProgressTracker {

    private OnboardingProgressTracker() {}

    public static JPanel build(OnboardingProgressState progress) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SURFACE);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.setMaximumSize(new Dimension(250, 500));

        // Title - aligned with items below
        JLabel title = label("Onboarding Progress", new Font("Segoe UI", Font.BOLD, 11), ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));

        // Steps
        panel.add(buildStep("Employee Selected", true));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Role Assigned", progress.isRoleAssigned()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Workflow Started", progress.isWorkflowStarted()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Forms Loaded", progress.isFormsLoaded()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Assets Allocated", progress.isAssetsAllocated()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Account Created", progress.isAccountCreated()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Training Assigned", progress.isTrainingAssigned()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Welcome Email Sent", progress.isAccessControlled()));

        return panel;
    }

    private static JPanel buildStep(String name, boolean completed) {
        JPanel step = new JPanel(new GridBagLayout());
        step.setOpaque(false);
        step.setMaximumSize(new Dimension(250, 24));

        Color color = completed ? new Color(0x4AC26B) : TEXT_SECONDARY;
        String checkmark = completed ? "[X]" : "[ ]";

        JLabel checkbox = new JLabel(checkmark);
        checkbox.setFont(new Font("Monospaced", Font.PLAIN, 10));
        checkbox.setForeground(color);
        checkbox.setPreferredSize(new Dimension(28, 16));

        JLabel stepName = label(name, FONT_SMALL, color);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.weightx = 0;
        step.add(checkbox, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        step.add(stepName, gbc);

        return step;
    }
}
