package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class ClearanceScreen {

    private ClearanceScreen() {}

    public static void show(EmployeeRecord emp) {
        JFrame frame = new JFrame("Clearance & Asset Verification — " + emp.name);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(emp, frame), BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Clearance & Asset Verification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    private static JPanel buildContent(EmployeeRecord emp, JFrame frame) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        content.add(buildEmployeeCard(emp));
        content.add(Box.createVerticalStrut(25));

        JCheckBox laptop  = styledCheckBox("Laptop returned");
        JCheckBox idCard  = styledCheckBox("ID card returned");
        JCheckBox access  = styledCheckBox("System access revoked");
        JCheckBox email   = styledCheckBox("Email disabled");
        JCheckBox finance = styledCheckBox("No pending dues");

        content.add(buildAssetCard(laptop, idCard, access, email, finance));
        content.add(Box.createVerticalStrut(25));
        content.add(buildButtonPanel(emp, frame, laptop, idCard, access, email, finance));
        return content;
    }

    private static JPanel buildEmployeeCard(EmployeeRecord emp) {
        JPanel empCard = new JPanel(new GridLayout(2, 2, 10, 10));
        empCard.setBackground(BG_SURFACE);
        empCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(15, 20, 15, 20)));
        empCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        empCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        empCard.add(label("Employee ID:", FONT_BOLD, TEXT_SECONDARY));
        empCard.add(label(emp.empId, FONT_UI, TEXT_PRIMARY));
        empCard.add(label("Department:", FONT_BOLD, TEXT_SECONDARY));
        empCard.add(label(emp.department, FONT_UI, TEXT_PRIMARY));
        return empCard;
    }

    private static JPanel buildAssetCard(JCheckBox laptop, JCheckBox idCard, JCheckBox access, JCheckBox email, JCheckBox finance) {
        JPanel assetCard = new JPanel();
        assetCard.setLayout(new BoxLayout(assetCard, BoxLayout.Y_AXIS));
        assetCard.setBackground(BG_SURFACE);
        assetCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(15, 20, 15, 20)));
        assetCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = label("Asset Verification Checklist", FONT_BOLD, TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        assetCard.add(heading);
        assetCard.add(Box.createVerticalStrut(10));

        for (JCheckBox cb : new JCheckBox[]{laptop, idCard, access, email, finance}) {
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            assetCard.add(cb);
            assetCard.add(Box.createVerticalStrut(4));
        }
        return assetCard;
    }

    private static JPanel buildButtonPanel(EmployeeRecord emp, JFrame frame, JCheckBox laptop, JCheckBox idCard, JCheckBox access, JCheckBox email, JCheckBox finance) {
        JButton cancelBtn = ghostButton("Cancel");
        JButton verifyBtn = primaryButton("Verify & Complete");

        cancelBtn.addActionListener(e -> frame.dispose());

        verifyBtn.addActionListener(e -> {
            if (!laptop.isSelected() || !idCard.isSelected() || !access.isSelected() || !email.isSelected() || !finance.isSelected()) {
                JOptionPane.showMessageDialog(frame, "All clearance items must be verified before proceeding.", "Clearance Incomplete", JOptionPane.WARNING_MESSAGE);
                return;
            }
            frame.dispose();
            OffboardingPage.completeTask(emp.empId, "Asset Clearance");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.add(cancelBtn);
        btnPanel.add(verifyBtn);
        return btnPanel;
    }
}
