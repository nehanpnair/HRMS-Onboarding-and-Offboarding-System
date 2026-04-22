package gui;

import model.model.PreOnboardingTask;
import model.model.PreOnboardingTask.TaskStatus;
import service.PreOnboardingService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Pre-Onboarding Overview Panel - SIMPLIFIED.
 * Shows only the 5 pre-onboarding steps with strict sequential workflow.
 * No onboarding logic mixed in.
 */
public final class PreOnboardingOverview {

    private PreOnboardingOverview() {}

    private static PreOnboardingService service;
    private static PreOnboardingProgressState progressState;
    private static JPanel tasksContainer;
    private static Runnable onCompletionCallback;

    /**
     * Build pre-onboarding overview
     */
    public static JPanel build(PreOnboardingService preOnboardingService, 
                                PreOnboardingProgressState progressState,
                                Runnable onProceedToOnboarding) {
        PreOnboardingOverview.service = preOnboardingService;
        PreOnboardingOverview.progressState = progressState;
        PreOnboardingOverview.onCompletionCallback = onProceedToOnboarding;

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(buildHeader(), BorderLayout.NORTH);

        tasksContainer = new JPanel();
        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setBackground(BG_APP);

        JScrollPane scroll = new JScrollPane(tasksContainer);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scroll, BorderLayout.CENTER);

        setupProgressListener();
        refreshTasksDisplay();

        return panel;
    }

    /**
     * Backward compatibility
     */
    public static JPanel build(PreOnboardingProgressState progress) {
        return build(null, progress, () -> {});
    }

    private static JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = label("Pre-Onboarding Checklist", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton sendReminderBtn = new JButton("Send Reminder");
        sendReminderBtn.setBackground(ACCENT);
        sendReminderBtn.setForeground(TEXT_PRIMARY);
        sendReminderBtn.setFont(FONT_BOLD);
        sendReminderBtn.setBorder(new EmptyBorder(8, 16, 8, 16));
        sendReminderBtn.setFocusPainted(false);
        sendReminderBtn.addActionListener(e -> sendReminder());
        header.add(sendReminderBtn, BorderLayout.EAST);

        return header;
    }

    private static void refreshTasksDisplay() {
        tasksContainer.removeAll();

        if (progressState.isAllCompleted()) {
            tasksContainer.add(buildCompletionSummary());
        } else {
            List<PreOnboardingTask> tasks = progressState.getTasks();
            for (PreOnboardingTask task : tasks) {
                tasksContainer.add(buildTaskCard(task));
                tasksContainer.add(Box.createVerticalStrut(8));
            }
        }

        tasksContainer.add(Box.createVerticalGlue());
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }

    private static JPanel buildTaskCard(PreOnboardingTask task) {
        JPanel card = card();
        card.setLayout(new BorderLayout(12, 12));
        card.setBackground(getBackgroundForStatus(task.getStatus()));

        // Left: Task info
        JPanel left = buildTaskInfo(task);
        card.add(left, BorderLayout.CENTER);

        // Right: Action button or status
        JPanel right = buildTaskAction(task);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private static JPanel buildTaskInfo(PreOnboardingTask task) {
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        // Title with status icon
        JPanel titleRow = new JPanel();
        titleRow.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        String statusIcon = getIconForStatus(task.getStatus());
        Color statusColor = getColorForStatus(task.getStatus());
        JLabel iconLabel = label(statusIcon, new Font("Segoe UI", Font.BOLD, 14), statusColor);
        JLabel titleLabel = label(task.getTitle(), FONT_BOLD, TEXT_PRIMARY);

        titleRow.add(iconLabel);
        titleRow.add(titleLabel);
        info.add(titleRow);
        info.add(Box.createVerticalStrut(4));

        // Description
        JLabel descLabel = label(task.getDescription(), FONT_SMALL, TEXT_MUTED);
        info.add(descLabel);
        info.add(Box.createVerticalStrut(4));

        // Due date
        String dueDateStr = "Due: " + task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        JLabel dateLabel = label(dueDateStr, FONT_SMALL, TEXT_MUTED);
        info.add(dateLabel);

        return info;
    }

    private static JPanel buildTaskAction(PreOnboardingTask task) {
        JPanel action = new JPanel();
        action.setLayout(new BoxLayout(action, BoxLayout.Y_AXIS));
        action.setOpaque(false);
        action.setBorder(new EmptyBorder(0, 12, 0, 0));

        TaskStatus status = task.getStatus();

        if (status == TaskStatus.COMPLETED) {
            JLabel completeLabel = label("✓", new Font("Segoe UI", Font.BOLD, 20), new Color(0x4AC26B));
            action.add(Box.createVerticalGlue());
            action.add(completeLabel);
            action.add(Box.createVerticalGlue());
        } 
        else if (status == TaskStatus.FAILED) {
            JButton retryBtn = new JButton("Retry");
            retryBtn.setBackground(new Color(0xFF6B6B));
            retryBtn.setForeground(TEXT_PRIMARY);
            retryBtn.setFont(FONT_SMALL);
            retryBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
            retryBtn.setFocusPainted(false);
            retryBtn.addActionListener(e -> executeTask(task.getTaskID()));
            action.add(Box.createVerticalGlue());
            action.add(retryBtn);
            action.add(Box.createVerticalGlue());
        } 
        else {
            // Check if this is the next pending task
            PreOnboardingTask nextPending = progressState.getNextPendingTask();
            boolean isNextTask = nextPending != null && nextPending.getTaskID().equals(task.getTaskID());

            if (isNextTask) {
                JButton executeBtn = new JButton("Start");
                executeBtn.setBackground(ACCENT);
                executeBtn.setForeground(TEXT_PRIMARY);
                executeBtn.setFont(FONT_BOLD);
                executeBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
                executeBtn.setFocusPainted(false);
                executeBtn.addActionListener(e -> executeTask(task.getTaskID()));
                action.add(Box.createVerticalGlue());
                action.add(executeBtn);
                action.add(Box.createVerticalGlue());
            } else {
                // Task is locked
                JLabel lockedLabel = label("⊘ Locked", FONT_SMALL, TEXT_SECONDARY);
                action.add(Box.createVerticalGlue());
                action.add(lockedLabel);
                action.add(Box.createVerticalGlue());
            }
        }

        return action;
    }

    private static JPanel buildCompletionSummary() {
        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setOpaque(false);
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);

        summary.add(Box.createVerticalStrut(40));
        
        JLabel celebrationLabel = new JLabel("🎉");
        celebrationLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        celebrationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.add(celebrationLabel);

        summary.add(Box.createVerticalStrut(20));
        JLabel messageLabel = label("Pre-Onboarding Completed!", 
                                     new Font("Segoe UI", Font.BOLD, 18), 
                                     new Color(0x4AC26B));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.add(messageLabel);

        summary.add(Box.createVerticalStrut(20));
        JLabel summaryLabel = label("All 5 pre-onboarding tasks completed successfully!", 
                                     FONT_SMALL, TEXT_MUTED);
        summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.add(summaryLabel);

        summary.add(Box.createVerticalStrut(30));
        JButton proceedBtn = new JButton("Proceed to Onboarding");
        proceedBtn.setBackground(ACCENT);
        proceedBtn.setForeground(TEXT_PRIMARY);
        proceedBtn.setFont(FONT_BOLD);
        proceedBtn.setBorder(new EmptyBorder(10, 24, 10, 24));
        proceedBtn.setFocusPainted(false);
        proceedBtn.setMaximumSize(new Dimension(250, 40));
        proceedBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedBtn.addActionListener(e -> {
            if (onCompletionCallback != null) {
                onCompletionCallback.run();
            }
        });
        summary.add(proceedBtn);

        summary.add(Box.createVerticalStrut(40));
        summary.add(Box.createVerticalGlue());

        return summary;
    }

    private static void executeTask(String taskID) {
        if (service == null) {
            JOptionPane.showMessageDialog(null, "Service not initialized", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            SwingUtilities.invokeLater(() -> {
                boolean success = service.executeTask(taskID);
                if (!success) {
                    JOptionPane.showMessageDialog(null, 
                        "Could not execute task. Check prerequisites.", 
                        "Task Execution Failed", 
                        JOptionPane.WARNING_MESSAGE);
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void setupProgressListener() {
        if (service == null) return;

        service.setProgressUpdateListener((taskID, status) -> {
            SwingUtilities.invokeLater(() -> {
                refreshTasksDisplay();
            });
        });
    }

    private static void sendReminder() {
        if (service == null) return;
        try {
            System.out.println("Sending pre-onboarding reminder...");
            JOptionPane.showMessageDialog(null, "Reminder sent to employee", "Reminder Sent", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Color getBackgroundForStatus(TaskStatus status) {
        switch (status) {
            case COMPLETED:
                return new Color(0x1F5C3E); // Dark green
            case IN_PROGRESS:
                return new Color(0x3D3A50); // Purple
            case FAILED:
                return new Color(0x5C3E3E); // Dark red
            case PENDING:
            default:
                return new Color(0x2D2D3D); // Dark gray
        }
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

    private static String getIconForStatus(TaskStatus status) {
        switch (status) {
            case COMPLETED:
                return "☑";
            case IN_PROGRESS:
                return "⟳";
            case FAILED:
                return "☒";
            case PENDING:
            default:
                return "☐";
        }
    }
}
