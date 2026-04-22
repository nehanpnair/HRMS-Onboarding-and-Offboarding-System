package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class DocumentScreen {

    private DocumentScreen() {}

    public static void show(EmployeeRecord emp) {
        JFrame frame = new JFrame("Document Generation - " + emp.name);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JCheckBox experienceCheck = styledCheckBox("Experience Certificate");
        JCheckBox relievingCheck = styledCheckBox("Relieving Letter");
        JCheckBox otherDocsCheck = styledCheckBox("Other Relieving Documents");

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(experienceCheck, relievingCheck, otherDocsCheck), BorderLayout.CENTER);
        root.add(buildButtonPanel(emp, frame, experienceCheck, relievingCheck, otherDocsCheck), BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Document Generation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    private static JPanel buildContent(JCheckBox experienceCheck, JCheckBox relievingCheck,
                                       JCheckBox otherDocsCheck) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SURFACE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = label("Generate Exit Documents", FONT_BOLD, TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(6));

        JLabel subheading = label(
            "This step is not complete until all 3 document items are ticked.",
            FONT_SMALL,
            TEXT_MUTED
        );
        subheading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subheading);
        card.add(Box.createVerticalStrut(12));
        card.add(hRule());
        card.add(Box.createVerticalStrut(12));

        card.add(experienceCheck);
        card.add(Box.createVerticalStrut(6));
        card.add(relievingCheck);
        card.add(Box.createVerticalStrut(6));
        card.add(otherDocsCheck);
        card.add(Box.createVerticalStrut(10));

        JLabel statusLabel = label("Not done - complete all 3 document items.", FONT_SMALL, TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);

        Runnable refreshStatus = () -> {
            boolean allDone = experienceCheck.isSelected() && relievingCheck.isSelected() && otherDocsCheck.isSelected();
            statusLabel.setText(allDone
                ? "All required offboarding documents are marked generated."
                : "Not done - complete all 3 document items.");
            statusLabel.setForeground(allDone ? GREEN : TEXT_MUTED);
        };

        experienceCheck.addActionListener(e -> refreshStatus.run());
        relievingCheck.addActionListener(e -> refreshStatus.run());
        otherDocsCheck.addActionListener(e -> refreshStatus.run());

        content.add(card);
        return content;
    }

    private static JPanel buildButtonPanel(EmployeeRecord emp, JFrame frame,
                                           JCheckBox experienceCheck, JCheckBox relievingCheck,
                                           JCheckBox otherDocsCheck) {
        JButton proceedBtn = primaryButton("Generate & Complete");
        proceedBtn.addActionListener(e -> {
            boolean allDone = experienceCheck.isSelected() && relievingCheck.isSelected() && otherDocsCheck.isSelected();
            if (!allDone) {
                JOptionPane.showMessageDialog(frame,
                    "You must tick all 3 document items before completing Document Generation.",
                    "Documents Incomplete",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            frame.dispose();
            OffboardingPage.completeTask(emp.empId, "Document Generation");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.add(proceedBtn);
        return btnPanel;
    }
}
