package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class ExitInterviewDialog {

    private ExitInterviewDialog() {}

    public static void show(EmployeeRecord emp) {
        JDialog dialog = new JDialog((Frame) null,
                "Exit Interview — " + emp.name, true);
        dialog.setSize(520, 520);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_CARD);
        root.setBorder(new EmptyBorder(24, 24, 20, 24));

        root.add(buildTitleBlock(emp), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildForm(emp, dialog));
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_CARD);

        root.add(scroll, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static JPanel buildTitleBlock(EmployeeRecord emp) {
        JPanel titleRow = new JPanel(new BorderLayout(0, 4));
        titleRow.setBackground(BG_CARD);
        titleRow.setBorder(new EmptyBorder(0, 0, 20, 0));

        titleRow.add(
            label("Exit Interview", new Font("Segoe UI", Font.BOLD, 15), TEXT_PRIMARY),
            BorderLayout.NORTH);

        titleRow.add(
            label("Please fill in the exit interview details for " + emp.name + ".",
                  FONT_SMALL, TEXT_MUTED),
            BorderLayout.SOUTH);

        return titleRow;
    }

    private static JPanel buildForm(EmployeeRecord emp, JDialog dialog) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_CARD);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);

        Map<String, JComponent> fieldMap = new HashMap<>();

        // Reason for leaving
        JComboBox<String> reasonBox = styledCombo(new String[]{
            "Better Opportunity", "Relocation", "Health Reasons", "Personal Reasons", "Other"
        });
        fieldMap.put("Reason for leaving", reasonBox);
        formPanel.add(formFieldBlock("Reason for leaving *", reasonBox));
        formPanel.add(Box.createVerticalStrut(10));

        // Feedback
        JTextArea feedbackArea = new JTextArea(4, 20);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBorder(new LineBorder(BORDER, 1, true));
        feedbackArea.setBackground(BG_SURFACE);
        feedbackArea.setForeground(TEXT_PRIMARY);
        fieldMap.put("Feedback", feedbackArea);
        formPanel.add(formFieldBlock("Experience Feedback", feedbackArea));
        formPanel.add(Box.createVerticalStrut(10));

        // Handover Status
        JComboBox<String> handoverBox = styledCombo(new String[]{
            "Completed", "In Progress", "Not Started"
        });
        fieldMap.put("Handover Status", handoverBox);
        formPanel.add(formFieldBlock("Handover Status", handoverBox));
        formPanel.add(Box.createVerticalStrut(10));

        wrapper.add(formPanel, BorderLayout.CENTER);
        wrapper.add(buildButtonRow(emp, dialog, fieldMap), BorderLayout.SOUTH);

        return wrapper;
    }

    private static JPanel buildButtonRow(EmployeeRecord emp, JDialog dialog, Map<String, JComponent> fieldMap) {
        JButton cancelBtn = ghostButton("Cancel");
        JButton saveBtn   = primaryButton("Save & Complete");

        cancelBtn.addActionListener(e -> dialog.dispose());

        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Exit Interview details recorded locally.");
            dialog.dispose();
            OffboardingPage.completeTask(emp.empId, "Exit Interview");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        return btnRow;
    }
}