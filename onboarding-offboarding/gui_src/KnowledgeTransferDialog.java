package gui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.io.File;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class KnowledgeTransferDialog {

    private KnowledgeTransferDialog() {}

    public static void show(EmployeeRecord emp) {
        JDialog dialog = new JDialog((Frame) null, "Knowledge Transfer — " + emp.name, true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(24, 28, 20, 28));

        root.add(buildTitleBlock(), BorderLayout.NORTH);

        final boolean[] uploaded = {false};
        final JLabel statusLabel = label("No document uploaded", FONT_SMALL, TEXT_MUTED);

        JTextArea commentsArea = buildCommentsArea();
        root.add(buildCentrePanel(dialog, uploaded, statusLabel, commentsArea), BorderLayout.CENTER);
        root.add(buildButtonRow(emp, dialog, uploaded, commentsArea), BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private static JLabel buildTitleBlock() {
        JLabel title = label("Upload Knowledge Transfer Report", FONT_BOLD, TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        return title;
    }

    private static JPanel buildCentrePanel(JDialog dialog, boolean[] uploaded, JLabel statusLabel, JTextArea commentsArea) {
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(BG_APP);

        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        uploadRow.setBackground(BG_APP);
        uploadRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton uploadBtn = ghostButton("Choose File…");
        uploadRow.add(uploadBtn);
        uploadRow.add(Box.createHorizontalStrut(12));
        uploadRow.add(statusLabel);

        uploadBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                statusLabel.setText("Uploaded: " + f.getName());
                statusLabel.setForeground(GREEN);
                uploaded[0] = true;
            }
        });

        centre.add(uploadRow);
        centre.add(Box.createVerticalStrut(20));

        JLabel commentsLbl = label("Additional Comments (Optional)", FONT_SMALL, TEXT_SECONDARY);
        commentsLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(commentsLbl);
        centre.add(Box.createVerticalStrut(6));

        JScrollPane scroll = new JScrollPane(commentsArea);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        centre.add(scroll);
        return centre;
    }

    private static JTextArea buildCommentsArea() {
        JTextArea area = new JTextArea(4, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FONT_UI);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(BG_SURFACE);
        area.setCaretColor(TEXT_PRIMARY);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    private static JPanel buildButtonRow(EmployeeRecord emp, JDialog dialog, boolean[] uploaded, JTextArea commentsArea) {
        JButton cancelBtn = ghostButton("Cancel");
        JButton verifyBtn = primaryButton("Verify & Complete");

        cancelBtn.addActionListener(e -> dialog.dispose());

        verifyBtn.addActionListener(e -> {
            if (!uploaded[0]) {
                JOptionPane.showMessageDialog(dialog, "Please upload the report before proceeding.", "Missing Document", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.dispose();
            OffboardingPage.completeTask(emp.empId, "Knowledge Transfer");
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(BG_APP);
        btnRow.setBorder(new EmptyBorder(16, 0, 0, 0));
        btnRow.add(cancelBtn);
        btnRow.add(verifyBtn);
        return btnRow;
    }
}
