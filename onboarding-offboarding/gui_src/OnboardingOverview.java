package gui;

import model.model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Overview panel showing employee list with selection.
 */
public final class OnboardingOverview {

    private OnboardingOverview() {}

    public static JPanel build(List<Employee> employees, Employee selected, Consumer<Employee> onSelect) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JLabel header = label("Select Employee", new Font("Segoe UI", Font.BOLD, 14), TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_APP);

        if (employees == null || employees.isEmpty()) {
            content.add(label("No employees found", FONT_UI, TEXT_MUTED));
        } else {
            for (Employee emp : employees) {
                boolean isSelected = selected != null && selected.getEmployeeID().equals(emp.getEmployeeID());
                content.add(buildEmployeeCard(emp, isSelected, onSelect));
                content.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_APP);
        scroll.getViewport().setBackground(BG_APP);
        scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel buildEmployeeCard(Employee emp, boolean selected, Consumer<Employee> onSelect) {
        JPanel card = card();
        card.setLayout(new BorderLayout(12, 0));
        if (selected) {
            card.setBackground(new Color(0x2E2E2E));
        }

        // Employee info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel name = label(emp.getName(), FONT_BOLD, TEXT_PRIMARY);
        JLabel id = label("ID: " + emp.getEmployeeID(), FONT_SMALL, TEXT_SECONDARY);
        JLabel dept = label("Dept: " + emp.getDepartment(), FONT_SMALL, TEXT_SECONDARY);

        name.setAlignmentX(Component.LEFT_ALIGNMENT);
        id.setAlignmentX(Component.LEFT_ALIGNMENT);
        dept.setAlignmentX(Component.LEFT_ALIGNMENT);

        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(id);
        info.add(dept);

        card.add(info, BorderLayout.CENTER);

        // Status indicator
        JPanel status = new JPanel();
        status.setOpaque(false);
        status.setPreferredSize(new Dimension(40, 40));
        JLabel indicator = new JLabel("●");
        indicator.setFont(new Font("Segoe UI", Font.BOLD, 20));
        indicator.setForeground("ACTIVE".equals(emp.getStatus()) ? new Color(0x4AC26B) : new Color(0xFF6B6B));
        status.add(indicator);
        card.add(status, BorderLayout.EAST);

        // Click to select
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onSelect.accept(emp); }
        });

        return card;
    }
}
