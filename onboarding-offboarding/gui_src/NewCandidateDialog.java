package gui;

import model.model.Candidate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.UUID;

import static gui.Theme.*;

public class NewCandidateDialog extends JDialog {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField skillsField;
    private JComboBox<String> statusCombo;
    private boolean isSubmitted = false;
    private Candidate candidate;

    public NewCandidateDialog(Frame owner) {
        super(owner, "Add New Candidate", true);
        setupUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("New Candidate");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 16, 16));
        form.setBackground(BG_APP);

        nameField = createField("Full Name");
        emailField = createField("Email");
        skillsField = createField("Skills");
        
        statusCombo = new JComboBox<>(new String[]{"PENDING", "PRE_ONBOARDING"});
        statusCombo.setBackground(BG_SURFACE);
        statusCombo.setForeground(TEXT_PRIMARY);

        form.add(createLabel("Full Name")); form.add(nameField);
        form.add(createLabel("Email")); form.add(emailField);
        form.add(createLabel("Skills")); form.add(skillsField);
        form.add(createLabel("Status")); form.add(statusCombo);

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBackground(BG_APP);
        btnPanel.setBorder(new EmptyBorder(24, 0, 0, 0));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(0x3A3A42));
        cancelBtn.setForeground(TEXT_SECONDARY);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dispose());

        JButton addBtn = new JButton("Add Candidate");
        addBtn.setBackground(ACCENT);
        addBtn.setForeground(TEXT_PRIMARY);
        addBtn.setFont(FONT_BOLD);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> submit());

        btnPanel.add(cancelBtn);
        btnPanel.add(addBtn);

        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_SECONDARY);
        l.setFont(FONT_UI);
        return l;
    }

    private JTextField createField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(BG_SURFACE);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void submit() {
        if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        candidate = new Candidate(
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                nameField.getText().trim(),
                emailField.getText().trim(),
                skillsField.getText().trim(),
                null,
                null,
                statusCombo.getSelectedItem().toString()
        );

        isSubmitted = true;
        dispose();
    }

    public boolean isSubmitted() { return isSubmitted; }
    public Candidate getCandidate() { return candidate; }
}
