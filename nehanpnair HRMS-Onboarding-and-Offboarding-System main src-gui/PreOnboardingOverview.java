package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Overview panel showing pre-onboarding tasks with status and dates.
 * Displays the task list from the screenshot with proper styling.
 */
public final class PreOnboardingOverview {

    private PreOnboardingOverview() {}

    // Task data model
    private static class PreOnboardingTask {
        String title;
        String description;
        boolean completed;
        String category;
        String dueDate;
        String sentDate;
        String completedDate;

        PreOnboardingTask(String title, String desc, boolean completed, String category,
                         String due, String sent, String completedDt) {
            this.title = title;
            this.description = desc;
            this.completed = completed;
            this.category = category;
            this.dueDate = due;
            this.sentDate = sent;
            this.completedDate = completedDt;
        }
    }

    public static JPanel build(PreOnboardingProgressState progress, Runnable onTaskUpdated) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header with title and button
        panel.add(buildHeader(), BorderLayout.NORTH);

        // Tasks content
        panel.add(buildTasksContent(progress, onTaskUpdated), BorderLayout.CENTER);

        return panel;
    }
    
    // Overload for backward compatibility
    public static JPanel build(PreOnboardingProgressState progress) {
        return build(progress, () -> {});
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
        header.add(sendReminderBtn, BorderLayout.EAST);

        return header;
    }

    private static JComponent buildTasksContent(PreOnboardingProgressState progress, Runnable onTaskUpdated) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        // Create tasks based on progress state
        PreOnboardingTask[] tasks = new PreOnboardingTask[] {
            new PreOnboardingTask(
                "Send Welcome Email",
                "Welcome email with company overview and what to expect",
                true, // First task is always completed
                "Communication",
                "2026-02-16", "2026-02-15", "2026-02-15"
            ),
            new PreOnboardingTask(
                "Share Pre-boarding Portal Access",
                "Send credentials for employee self-service portal",
                progress.isPortalAccessShared(),
                "Documentation",
                "2026-02-16", "2026-02-15", progress.isPortalAccessShared() ? "2026-02-16" : null
            ),
            new PreOnboardingTask(
                "Request Document Submission",
                "Request ID, certificates, and other required documents",
                progress.isDocumentSubmissionRequested(),
                "Documentation",
                "2026-02-18", "2026-02-16", progress.isDocumentSubmissionRequested() ? "2026-02-17" : null
            ),
            new PreOnboardingTask(
                "Schedule First Day Orientation",
                "Set up calendar invite for day 1 orientation session",
                progress.isOrientationScheduled(),
                "Preparation",
                "2026-02-20", "2026-02-18", progress.isOrientationScheduled() ? "2026-02-18" : null
            ),
            new PreOnboardingTask(
                "Prepare Workstation",
                "Coordinate with IT for laptop and desk setup",
                progress.isWorkstationPrepared(),
                "Preparation",
                "2026-02-24", "2026-02-20", null // This one might not be completed
            )
        };

        for (int i = 0; i < tasks.length; i++) {
            final int taskIndex = i;
            content.add(buildTaskCard(tasks[i], () -> toggleTask(taskIndex, progress, onTaskUpdated)));
            content.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    private static JPanel buildTaskCard(PreOnboardingTask task, Runnable onClicked) {
        JPanel card = card();
        card.setLayout(new BorderLayout(12, 0));
        card.setBackground(task.completed ? new Color(0x1F5C3E) : new Color(0x2D2D3D));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Make card clickable to toggle completion
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onClicked.run();
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(task.completed ? new Color(0x245C3E) : new Color(0x353545));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(task.completed ? new Color(0x1F5C3E) : new Color(0x2D2D3D));
            }
        });

        // Left: Checkmark and title
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        // Checkmark and title
        JPanel titleRow = new JPanel();
        titleRow.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        String checkmark = task.completed ? "☑" : "☐";
        Color checkColor = task.completed ? new Color(0x4AC26B) : TEXT_SECONDARY;
        JLabel checkLabel = label(checkmark, new Font("Segoe UI", Font.BOLD, 14), checkColor);
        JLabel titleLabel = label(task.title, FONT_BOLD, task.completed ? new Color(0x4AC26B) : TEXT_PRIMARY);

        titleRow.add(checkLabel);
        titleRow.add(titleLabel);

        JLabel descLabel = label(task.description, FONT_SMALL, TEXT_MUTED);
        JLabel categoryLabel = label(task.category, FONT_SMALL, TEXT_MUTED);

        left.add(titleRow);
        left.add(Box.createVerticalStrut(4));
        left.add(descLabel);
        left.add(Box.createVerticalStrut(3));
        left.add(categoryLabel);

        card.add(left, BorderLayout.CENTER);

        // Right: Dates
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(0, 12, 0, 0));

        JPanel dateRow1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dateRow1.setOpaque(false);
        dateRow1.add(label("Due: ", FONT_SMALL, TEXT_MUTED));
        dateRow1.add(label(task.dueDate, FONT_SMALL, TEXT_SECONDARY));

        JPanel dateRow2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dateRow2.setOpaque(false);
        dateRow2.add(label("Sent: ", FONT_SMALL, TEXT_MUTED));
        dateRow2.add(label(task.sentDate, FONT_SMALL, TEXT_SECONDARY));

        JPanel dateRow3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        dateRow3.setOpaque(false);
        if (task.completedDate != null) {
            dateRow3.add(label("Completed: ", FONT_SMALL, TEXT_MUTED));
            dateRow3.add(label(task.completedDate, FONT_SMALL, new Color(0x4AC26B)));
        } else {
            dateRow3.add(label("Pending", FONT_SMALL, TEXT_MUTED));
        }

        right.add(dateRow1);
        right.add(Box.createVerticalStrut(3));
        right.add(dateRow2);
        right.add(Box.createVerticalStrut(3));
        right.add(dateRow3);

        card.add(right, BorderLayout.EAST);

        return card;
    }

    private static void toggleTask(int taskIndex, PreOnboardingProgressState progress, Runnable onTaskUpdated) {
        // Toggle the corresponding task
        switch (taskIndex) {
            case 0:
                // First task (Send Welcome Email) is always completed, no toggle needed
                break;
            case 1:
                progress.setPortalAccessShared(!progress.isPortalAccessShared());
                break;
            case 2:
                progress.setDocumentSubmissionRequested(!progress.isDocumentSubmissionRequested());
                break;
            case 3:
                progress.setOrientationScheduled(!progress.isOrientationScheduled());
                break;
            case 4:
                progress.setWorkstationPrepared(!progress.isWorkstationPrepared());
                break;
        }
        // Notify that task was updated
        onTaskUpdated.run();
    }
}
