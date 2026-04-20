package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Visual progress tracker showing pre-onboarding task completion.
 */
public final class PreOnboardingProgressTracker {

    private PreOnboardingProgressTracker() {}

    public static JPanel build(PreOnboardingProgressState progress) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SURFACE);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.setMaximumSize(new Dimension(250, 500));

        // Title - aligned with items below
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(250, 24));

        JLabel title = label("Pre-Onboarding Progress", new Font("Segoe UI", Font.BOLD, 11), ACCENT);

        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.anchor = GridBagConstraints.WEST;
        tgbc.gridy = 0;

        // Spacer matching checkbox width
        JLabel spacer = new JLabel();
        spacer.setPreferredSize(new Dimension(28, 16));
        tgbc.gridx = 0;
        tgbc.insets = new Insets(0, 0, 0, 8);
        tgbc.weightx = 0;
        titlePanel.add(spacer, tgbc);

        tgbc.gridx = 1;
        tgbc.insets = new Insets(0, 0, 0, 0);
        tgbc.weightx = 1.0;
        tgbc.fill = GridBagConstraints.HORIZONTAL;
        titlePanel.add(title, tgbc);
        panel.add(titlePanel);
        panel.add(Box.createVerticalStrut(6));

        // Steps
        panel.add(buildStep("Send Welcome Email", true));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Share Pre-boarding Portal Access", progress.isPortalAccessShared()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Request Document Submission", progress.isDocumentSubmissionRequested()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Schedule First Day Orientation", progress.isOrientationScheduled()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(buildStep("Prepare Workstation", progress.isWorkstationPrepared()));

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
