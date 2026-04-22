package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.*;
import java.util.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Activity log panel showing system events during onboarding.
 */
public final class ActivityLogPanel {

    private static final java.util.List<String> logEntries = new java.util.ArrayList<>();
    private static JTextArea logArea;

    private ActivityLogPanel() {}

    public static JPanel build() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_SURFACE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setPreferredSize(new Dimension(270, 200));

        // Title
        JLabel title = label("System Activity Log", new Font("Segoe UI", Font.BOLD, 11), ACCENT);
        panel.add(title, BorderLayout.NORTH);

        // Log text area
        logArea = new JTextArea();
        logArea.setBackground(BG_APP);
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // Add initial logs
        addLog("System initialized");
        addLog("Database connected");
        addLog("Employee module ready");

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBackground(BG_APP);
        scrollPane.getViewport().setBackground(BG_APP);
        scrollPane.setBorder(new LineBorder(new Color(0x3A3A40), 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void addLog(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String logEntry = "[" + timestamp + "] " + message;
        logEntries.add(logEntry);
        
        if (logArea != null) {
            logArea.append(logEntry + "\n");
            // Auto-scroll to bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    public static void clearLogs() {
        logEntries.clear();
        if (logArea != null) {
            logArea.setText("");
        }
    }

    public static java.util.List<String> getLogs() {
        return new java.util.ArrayList<>(logEntries);
    }
}
