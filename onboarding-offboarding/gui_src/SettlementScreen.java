package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static gui.Theme.*;
import static gui.UIFactory.*;

public final class SettlementScreen {

    private SettlementScreen() {}

    public static void show(EmployeeRecord emp) {
        JFrame frame = new JFrame("Final Settlement — " + emp.name);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        double total = calculateSettlement(emp);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(30, 40, 20, 40));

        root.add(buildTitle(), BorderLayout.NORTH);
        root.add(buildContent(emp, total), BorderLayout.CENTER);
        root.add(buildButtonPanel(emp, frame), BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static double calculateSettlement(EmployeeRecord emp) {
        double monthlySalary  = 50_000;
        double dailySalary    = monthlySalary / 22;
        double salary         = dailySalary * 20;
        double leave          = 10 * dailySalary;
        double severance      = 0;
        
        if ("LAYOFF".equalsIgnoreCase(emp.exitType)) severance = monthlySalary;
        if ("VRS".equalsIgnoreCase(emp.exitType))    severance = 3 * monthlySalary * 2;
        
        double reimbursements = 0.01 * monthlySalary;
        return salary + leave + severance + reimbursements;
    }

    private static JLabel buildTitle() {
        JLabel title = new JLabel("Final Settlement");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    private static JPanel buildContent(EmployeeRecord emp, double total) {
        double monthlySalary  = 50_000;
        double dailySalary    = monthlySalary / 22;
        double salary         = dailySalary * 20;
        double leave          = 10 * dailySalary;
        double severance      = 0;
        if ("LAYOFF".equalsIgnoreCase(emp.exitType)) severance = monthlySalary;
        if ("VRS".equalsIgnoreCase(emp.exitType))    severance = 3 * monthlySalary * 2;
        double reimbursements = 0.01 * monthlySalary;
        double deductions     = 500; 

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

        JLabel heading = label("Settlement Breakdown", FONT_BOLD, TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(hRule());
        card.add(Box.createVerticalStrut(12));

        addBreakdownRow(card, "Earned Salary",    salary);
        addBreakdownRow(card, "Leave Encashment", leave);
        addBreakdownRow(card, "Severance",        severance);
        addBreakdownRow(card, "Reimbursements",   reimbursements);
        addBreakdownRow(card, "Deductions",       deductions);

        card.add(Box.createVerticalStrut(12));
        card.add(hRule());
        card.add(Box.createVerticalStrut(10));

        JLabel totalLabel = label("TOTAL:  ₹" + fmt(total - deductions),
                new Font("Segoe UI", Font.BOLD, 15), GREEN);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalLabel);

        content.add(card);
        return content;
    }

    private static void addBreakdownRow(JPanel card, String rowLabel, double amount) {
        JPanel row = new JPanel(new BorderLayout(40, 0));
        row.setBackground(BG_SURFACE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        row.add(label(rowLabel, FONT_UI, TEXT_SECONDARY), BorderLayout.WEST);
        row.add(label("₹" + fmt(amount), FONT_UI, TEXT_PRIMARY), BorderLayout.EAST);
        card.add(row);
        card.add(Box.createVerticalStrut(6));
    }

    private static JPanel buildButtonPanel(EmployeeRecord emp, JFrame frame) {
        JButton proceedBtn = primaryButton("Confirm & Complete");
        proceedBtn.addActionListener(e -> {
            frame.dispose();
            OffboardingPage.completeTask(emp.empId, "Final Settlement");
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.add(proceedBtn);
        return btnPanel;
    }

    private static String fmt(double val) {
        return String.format("%.2f", val);
    }
}
