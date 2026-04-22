package gui;

import model.model.Candidate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static gui.Theme.*;
import static gui.UIFactory.label;

public class CandidateListView {

    public static JPanel build(Consumer<Candidate> onSelect) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_APP);
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = label("Candidates (Pre-Onboarding)", new Font("Segoe UI", Font.BOLD, 22), TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("Add New Candidate");
        addBtn.setBackground(ACCENT);
        addBtn.setForeground(TEXT_PRIMARY);
        addBtn.setFont(FONT_BOLD);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            NewCandidateDialog dialog = new NewCandidateDialog(null);
            dialog.setVisible(true);
            if (dialog.isSubmitted()) {
                UnifiedDatabaseManager.insertCandidate(dialog.getCandidate());
                // Refresh list
                panel.removeAll();
                panel.add(build(onSelect), BorderLayout.CENTER);
                panel.revalidate();
                panel.repaint();
            }
        });
        header.add(addBtn, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        // List
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_APP);

        List<Candidate> candidates = UnifiedDatabaseManager.getAllCandidates();
        if (candidates.isEmpty()) {
            JLabel empty = label("No candidates found.", FONT_UI, TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(Box.createVerticalStrut(40));
            listContainer.add(empty);
        } else {
            for (Candidate c : candidates) {
                listContainer.add(buildCandidateCard(c, onSelect, panel));
                listContainer.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_APP);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel buildCandidateCard(Candidate c, Consumer<Candidate> onSelect, JPanel parent) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(BG_SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Info
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setBackground(BG_SURFACE);
        info.add(label(c.getName() + " (" + c.getCandidateID() + ")", FONT_BOLD, TEXT_PRIMARY));
        info.add(label(c.getEmail() + " • " + c.getSkills() + " • Status: " + c.getStatus(), FONT_SMALL, TEXT_SECONDARY));

        card.add(info, BorderLayout.CENTER);

        // Action
        JButton selectBtn = new JButton(c.getStatus().equals("PRE_ONBOARDING_DONE") ? "Migrate to Employees" : "Process Pre-Onboarding");
        selectBtn.setBackground(new Color(0x3A3A42));
        selectBtn.setForeground(TEXT_PRIMARY);
        selectBtn.setFocusPainted(false);
        selectBtn.addActionListener(e -> {
            if (c.getStatus().equals("PRE_ONBOARDING_DONE")) {
                UnifiedDatabaseManager.migrateCandidateToEmployee(c);
                JOptionPane.showMessageDialog(null, "Migrated candidate to employees successfully!");
                parent.removeAll();
                parent.add(build(onSelect), BorderLayout.CENTER);
                parent.revalidate();
                parent.repaint();
            } else {
                onSelect.accept(c);
            }
        });
        card.add(selectBtn, BorderLayout.EAST);

        return card;
    }
}
