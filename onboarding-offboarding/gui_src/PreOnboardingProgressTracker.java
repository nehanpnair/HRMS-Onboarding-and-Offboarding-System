package gui;

import model.model.PreOnboardingTask;
import model.model.PreOnboardingTask.TaskStatus;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Pre-Onboarding Progress Tracker for left sidebar.
 * Shows only the 5 pre-onboarding steps with status indicators.
 */
public final class PreOnboardingProgressTracker {

    private PreOnboardingProgressTracker() {}

    public static JPanel build(PreOnboardingProgressState progress) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SURFACE);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.setMaximumSize(new Dimension(250, 500));

        // Title
        JLabel title = label("Pre-Onboarding Progress", new Font("Segoe UI", Font.BOLD, 11), ACCENT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));

        // Get tasks from state
        List<PreOnboardingTask> tasks = progress.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            PreOnboardingTask task = tasks.get(i);
            panel.add(buildStep(task));
            if (i < tasks.size() - 1) {
                panel.add(Box.createVerticalStrut(6));
            }
        }

        // Completion percentage
        panel.add(Box.createVerticalStrut(12));
        int percentage = progress.getCompletionPercentage();
        JLabel percentLabel = label(percentage + "% Complete", FONT_SMALL, TEXT_SECONDARY);
        percentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(percentLabel);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private static JPanel buildStep(PreOnboardingTask task) {
        JPanel step = new JPanel(new GridBagLayout());
        step.setOpaque(false);
        step.setMaximumSize(new Dimension(250, 24));

        TaskStatus status = task.getStatus();
        Color color = getColorForStatus(status);
        String checkbox = getCheckboxForStatus(status);

        JLabel checkboxLabel = new JLabel(checkbox);
        checkboxLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        checkboxLabel.setForeground(color);
        checkboxLabel.setPreferredSize(new Dimension(28, 16));

        String displayText = task.getTitle();
        if (status == TaskStatus.IN_PROGRESS) {
            displayText += " (...)";
        } else if (status == TaskStatus.FAILED) {
            displayText += " ✗";
        }
        
        JLabel taskLabel = label(displayText, FONT_SMALL, color);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.weightx = 0;
        step.add(checkboxLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        step.add(taskLabel, gbc);

        return step;
    }

    private static Color getColorForStatus(TaskStatus status) {
        switch (status) {
            case COMPLETED:
                return new Color(0x4AC26B); // Green
            case IN_PROGRESS:
                return ACCENT; // Blue/Purple
            case FAILED:
                return new Color(0xFF6B6B); // Red
            case PENDING:
            default:
                return TEXT_SECONDARY;
        }
    }

    private static String getCheckboxForStatus(TaskStatus status) {
        switch (status) {
            case COMPLETED:
                return "[X]";
            case IN_PROGRESS:
                return "[→]";
            case FAILED:
                return "[✗]";
            case PENDING:
            default:
                return "[ ]";
        }
    }
}
