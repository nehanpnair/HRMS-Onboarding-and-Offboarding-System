package gui;

import model.model.Employee;
import strategy.TrainingStrategy;
import strategy.MandatoryTrainingStrategy;
import strategy.OptionalTrainingStrategy;
import proxy.RoleAccessProxy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import static gui.Theme.*;
import static gui.UIFactory.*;

public class OnboardingDetailPanel {

    private static Employee emp;
    private static OnboardingProgressState progress;
    private static Consumer<Employee> onComplete;
    private static Runnable onProgressChanged;

    // State panels to update dynamically
    private static JPanel configOutputPanel;
    private static JLabel assignedTrainingLabel;
    private static JLabel allocatedAssetsLabel;
    private static JLabel loadedFormsLabel;

    private static JCheckBox idProofCheck;
    private static JCheckBox docVerifyCheck;
    private static JCheckBox complianceCheck;

    private static JPanel hrNode;
    private static JPanel itNode;
    private static JPanel mgrNode;
    private static JLabel hrStatus;
    private static JLabel itStatus;
    private static JLabel mgrStatus;

    private static JPanel formsPanel;
    private static JPanel workflowPanel;
    private static JPanel experiencePanel;
    private static JPanel completionPanel;
    private static JButton applyBtn;
    private static JButton startWorkflowBtn;
    private static JButton sendWelcomeEmailBtn;
    private static JButton completeBtn;
    private static JLabel formsGateLabel;
    private static JLabel workflowGateLabel;
    private static JLabel experienceGateLabel;
    private static JLabel completionGateLabel;

    public static JPanel build(Employee selectedEmployee, OnboardingProgressState progressState,
                               Consumer<Employee> onCompleteCallback, Runnable onProgressChangedCallback) {
        emp = selectedEmployee;
        progress = progressState;
        onComplete = onCompleteCallback;
        onProgressChanged = onProgressChangedCallback;

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(BG_APP);
        mainContainer.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Build Sections
        mainContainer.add(buildSection1Summary());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(buildSection2Configuration());
        mainContainer.add(Box.createVerticalStrut(20));
        
        configOutputPanel = buildSection3ConfigOutput();
        configOutputPanel.setVisible(false); // Hidden until applied
        mainContainer.add(configOutputPanel);
        mainContainer.add(Box.createVerticalStrut(20));

        mainContainer.add(buildSection5Forms());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(buildSection4Workflow());
        mainContainer.add(Box.createVerticalStrut(20));
        mainContainer.add(buildSection6Experience());
        mainContainer.add(Box.createVerticalStrut(30));
        mainContainer.add(buildSection7Completion());

        // Make it scrollable
        JScrollPane scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBackground(BG_APP);
        scrollPane.getViewport().setBackground(BG_APP);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_APP);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        refreshStepAvailability();
        return wrapper;
    }

    private static JPanel buildSection1Summary() {
        JPanel panel = card();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel title = label("Employee Summary", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        JLabel name = label("Name: " + emp.getName(), FONT_UI, TEXT_SECONDARY);
        JLabel email = label("Email: " + emp.getEmail(), FONT_UI, TEXT_SECONDARY);
        JLabel status = label("Status: " + emp.getStatus(), FONT_UI, new Color(0x4AC26B));

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(name);
        panel.add(email);
        panel.add(status);
        return panel;
    }

    private static JPanel buildSection2Configuration() {
        JPanel panel = card();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Role-Based Configuration", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        JPanel form = new JPanel(new GridLayout(5, 2, 16, 12));
        form.setOpaque(false);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.setMaximumSize(new Dimension(600, 200));

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Software Engineer", "HR Manager", "Sales Executive"});
        JComboBox<String> deptBox = new JComboBox<>(new String[]{"Engineering", "Human Resources", "Sales"});
        JComboBox<String> trainingBox = new JComboBox<>(new String[]{"Mandatory Training", "Optional Training"});
        JComboBox<String> assetBox = new JComboBox<>(new String[]{"Standard Office", "Remote Working"});
        JComboBox<String> formBox = new JComboBox<>(new String[]{"Standard Onboarding Pack", "Executive Compliance Pack"});

        form.add(label("Role:", FONT_UI, TEXT_SECONDARY)); form.add(roleBox);
        form.add(label("Department:", FONT_UI, TEXT_SECONDARY)); form.add(deptBox);
        form.add(label("Training Strategy:", FONT_UI, TEXT_SECONDARY)); form.add(trainingBox);
        form.add(label("Asset Allocation:", FONT_UI, TEXT_SECONDARY)); form.add(assetBox);
        form.add(label("Form Type:", FONT_UI, TEXT_SECONDARY)); form.add(formBox);
        
        panel.add(form);
        panel.add(Box.createVerticalStrut(16));

        applyBtn = primaryButton("Apply Configuration");
        applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyBtn.addActionListener(e -> {
            // Apply Configuration using patterns without changing screen
            String selectedRole = (String) roleBox.getSelectedItem();
            String selectedDept = (String) deptBox.getSelectedItem();
            
            UnifiedDatabaseManager.updateEmployeeDetails(emp.getEmployeeID(), selectedDept, selectedRole);
            emp.setRole(selectedRole);
            emp.setDepartment(selectedDept);

            // 1. Factory Pattern Execution
            String assetSelection = (String) assetBox.getSelectedItem();
            Asset asset = AssetFactory.createAsset(assetSelection.split(" ")[0]);
            asset.allocate(emp);

            // 2. Strategy Pattern Execution
            boolean isMandatory = trainingBox.getSelectedIndex() == 0;
            TrainingStrategy strategy = isMandatory ? new MandatoryTrainingStrategy() : new OptionalTrainingStrategy();
            strategy.assignTraining(emp);

            // Update UI Outcomes
            String assetName = assetSelection.contains("Remote") ? "Laptop, VPN Token" : "Standard Office Desk, PC";
            assignedTrainingLabel.setText("Training Assigned: " + (isMandatory ? "Mandatory Compliance & Security" : "Optional Skill Development"));
            allocatedAssetsLabel.setText("Assets Allocated: " + assetName + " (Provisioned for " + selectedRole + ")");
            loadedFormsLabel.setText("Forms Loaded: " + formBox.getSelectedItem());

            configOutputPanel.setVisible(true);
            progress.setRoleAssigned(true);
            progress.setAssetsAllocated(true);
            progress.setTrainingAssigned(true);
            
            // Auto-check the loaded forms to simulate loading
            complianceCheck.setText("Forms Loaded (" + formBox.getSelectedItem() + ")");
            
            panel.revalidate();
            panel.getParent().revalidate();
            panel.getParent().repaint();
            notifyProgressChanged();
            refreshStepAvailability();
        });

        panel.add(applyBtn);
        return panel;
    }

    private static JPanel buildSection3ConfigOutput() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0x23262E)); // Slightly different background to highlight
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3B82F6), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Configuration Summary", new Font("Segoe UI", Font.BOLD, 14), ACCENT);
        assignedTrainingLabel = label("Training Assigned: Pending", FONT_UI, TEXT_PRIMARY);
        allocatedAssetsLabel = label("Assets Allocated: Pending", FONT_UI, TEXT_PRIMARY);
        loadedFormsLabel = label("Forms Loaded: Pending", FONT_UI, TEXT_PRIMARY);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(assignedTrainingLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(allocatedAssetsLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(loadedFormsLabel);

        return panel;
    }

    private static JPanel buildSection5Forms() {
        JPanel panel = card();
        formsPanel = panel;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Forms & Compliance Checklist", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(12));

        formsGateLabel = label("Complete role-based configuration to unlock this step.", FONT_SMALL, TEXT_MUTED);
        panel.add(formsGateLabel);
        panel.add(Box.createVerticalStrut(8));

        idProofCheck = createCheckbox("ID Proof Submitted");
        docVerifyCheck = createCheckbox("Employment Documents Verified");
        complianceCheck = createCheckbox("Compliance Forms Signed");

        panel.add(idProofCheck);
        panel.add(docVerifyCheck);
        panel.add(complianceCheck);

        return panel;
    }

    private static JCheckBox createCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(FONT_UI);
        cb.setForeground(TEXT_SECONDARY);
        cb.setBackground(BG_SURFACE);
        cb.setFocusPainted(false);
        cb.addActionListener(e -> {
            boolean allFormsChecked = idProofCheck.isSelected() && docVerifyCheck.isSelected() && complianceCheck.isSelected();
            boolean wasCompleted = progress.isFormsLoaded();
            progress.setFormsLoaded(allFormsChecked);
            if (allFormsChecked && !wasCompleted) {
                JOptionPane.showMessageDialog(null, "Forms & Compliance completed successfully!");
            }
            if (!allFormsChecked && wasCompleted) {
                refreshStepAvailability();
                notifyProgressChanged();
                return;
            }
            if (allFormsChecked != wasCompleted) {
                refreshStepAvailability();
                notifyProgressChanged();
            }
            if (allFormsChecked) {
                progress.setFormsLoaded(true);
            }
        });
        return cb;
    }

    private static JPanel buildSection4Workflow() {
        JPanel panel = card();
        workflowPanel = panel;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Workflow Automation", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        workflowGateLabel = label("Complete all Forms & Compliance items to unlock this step.", FONT_SMALL, TEXT_MUTED);
        panel.add(workflowGateLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel pipeline = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pipeline.setOpaque(false);
        pipeline.setAlignmentX(Component.LEFT_ALIGNMENT);

        hrStatus = label("Pending", FONT_SMALL, TEXT_MUTED);
        hrNode = buildNode("HR Approval", hrStatus);
        
        JLabel arrow1 = label(" ➔ ", FONT_BOLD, TEXT_MUTED);

        itStatus = label("Pending", FONT_SMALL, TEXT_MUTED);
        itNode = buildNode("IT Provisioning", itStatus);
        
        JLabel arrow2 = label(" ➔ ", FONT_BOLD, TEXT_MUTED);

        mgrStatus = label("Pending", FONT_SMALL, TEXT_MUTED);
        mgrNode = buildNode("Manager Welcome", mgrStatus);

        pipeline.add(hrNode);
        pipeline.add(arrow1);
        pipeline.add(itNode);
        pipeline.add(arrow2);
        pipeline.add(mgrNode);

        panel.add(pipeline);
        panel.add(Box.createVerticalStrut(20));

        startWorkflowBtn = primaryButton("Start Workflow");
        startWorkflowBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        startWorkflowBtn.addActionListener(e -> {
            startWorkflowBtn.setEnabled(false);
            // Simulate the Chain of Responsibility step by step
            simulateWorkflow();
        });
        panel.add(startWorkflowBtn);

        return panel;
    }

    private static JPanel buildNode(String name, JLabel statusLabel) {
        JPanel node = new JPanel();
        node.setLayout(new BoxLayout(node, BoxLayout.Y_AXIS));
        node.setBackground(BG_APP);
        node.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        JLabel nameLbl = label(name, FONT_UI, TEXT_PRIMARY);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        node.add(nameLbl);
        node.add(Box.createVerticalStrut(4));
        node.add(statusLabel);
        return node;
    }

    private static void simulateWorkflow() {
        // Step 1: HR
        updateNodeState(hrNode, hrStatus, "In Progress", new Color(0xEAB308));
        Timer t1 = new Timer(1000, e -> {
            updateNodeState(hrNode, hrStatus, "Approved", new Color(0x4AC26B));
            // Step 2: IT
            updateNodeState(itNode, itStatus, "Provisioning", new Color(0xEAB308));
        });
        t1.setRepeats(false); t1.start();

        Timer t2 = new Timer(2500, e -> {
            updateNodeState(itNode, itStatus, "Completed", new Color(0x4AC26B));
            // Step 3: Manager
            updateNodeState(mgrNode, mgrStatus, "Notifying", new Color(0xEAB308));
        });
        t2.setRepeats(false); t2.start();

        Timer t3 = new Timer(4000, e -> {
            updateNodeState(mgrNode, mgrStatus, "Sent", new Color(0x4AC26B));
            
            // Execute actual backend chain
            OnboardingHandler hr = new HRApprovalHandler();
            OnboardingHandler it = new ITProvisioningHandler();
            OnboardingHandler mgr = new ManagerWelcomeHandler();
            hr.setNext(it);
            it.setNext(mgr);
            hr.process(emp);

            progress.setWorkflowStarted(true);
            progress.setAccountCreated(true);
            JOptionPane.showMessageDialog(null, "Workflow Automation completed successfully!");
            refreshStepAvailability();
            notifyProgressChanged();
        });
        t3.setRepeats(false); t3.start();
    }

    private static void updateNodeState(JPanel node, JLabel status, String text, Color color) {
        status.setText(text);
        status.setForeground(color);
        node.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        node.repaint();
    }

    private static JPanel buildSection6Experience() {
        JPanel panel = card();
        experiencePanel = panel;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label("Employee Experience", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        panel.add(title);
        panel.add(Box.createVerticalStrut(12));

        experienceGateLabel = label("Complete Workflow Automation to unlock this step.", FONT_SMALL, TEXT_MUTED);
        panel.add(experienceGateLabel);
        panel.add(Box.createVerticalStrut(8));

        JLabel buddyLabel = label("Assigned Buddy: John Doe (Senior " + (emp.getRole() != null ? emp.getRole() : "Staff") + ")", FONT_UI, TEXT_SECONDARY);
        panel.add(buddyLabel);
        panel.add(Box.createVerticalStrut(12));

        sendWelcomeEmailBtn = primaryButton("Send Welcome Email");
        sendWelcomeEmailBtn.addActionListener(e -> {
            progress.setAccessControlled(true);
            JOptionPane.showMessageDialog(null, "Welcome Email sent to " + emp.getEmail() + "!");
            refreshStepAvailability();
            notifyProgressChanged();
        });
        panel.add(sendWelcomeEmailBtn);

        return panel;
    }

    private static JPanel buildSection7Completion() {
        JPanel panel = new JPanel();
        completionPanel = panel;
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        completionGateLabel = label("Complete all onboarding steps to enable final onboarding.", FONT_SMALL, TEXT_MUTED);
        completionGateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(completionGateLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.setOpaque(false);

        completeBtn = new JButton("Mark as Onboarded");
        completeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        completeBtn.setBackground(new Color(0x4AC26B));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setFocusPainted(false);
        completeBtn.setBorder(new EmptyBorder(10, 24, 10, 24));
        completeBtn.addActionListener(e -> {
            if (!progress.isAllCompleted()) {
                JOptionPane.showMessageDialog(null,
                    "Finish each onboarding step in order before marking this employee as onboarded.",
                    "Onboarding Incomplete",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            UnifiedDatabaseManager.updateEmployeeStatus(emp.getEmployeeID(), "ONBOARDED");
            JOptionPane.showMessageDialog(null, emp.getName() + " has successfully completed onboarding!");
            if (onComplete != null) {
                onComplete.accept(emp);
            }
        });
        buttonRow.add(completeBtn);
        panel.add(buttonRow);
        return panel;
    }

    private static void refreshStepAvailability() {
        boolean configurationDone = progress.isRoleAssigned() && progress.isAssetsAllocated() && progress.isTrainingAssigned();
        boolean formsDone = progress.isFormsLoaded();
        boolean workflowDone = progress.isWorkflowStarted() && progress.isAccountCreated();
        boolean experienceDone = progress.isAccessControlled();

        setComponentsEnabled(formsPanel, configurationDone, formsGateLabel);
        setComponentsEnabled(workflowPanel, formsDone, workflowGateLabel);
        setComponentsEnabled(experiencePanel, workflowDone, experienceGateLabel);

        if (completionGateLabel != null) {
            completionGateLabel.setVisible(!progress.isAllCompleted());
        }
        if (completeBtn != null) {
            completeBtn.setEnabled(progress.isAllCompleted());
        }
        if (startWorkflowBtn != null && workflowDone) {
            startWorkflowBtn.setEnabled(false);
        }
        if (sendWelcomeEmailBtn != null && experienceDone) {
            sendWelcomeEmailBtn.setEnabled(false);
        }
    }

    private static void setComponentsEnabled(JPanel panel, boolean enabled, JLabel gateLabel) {
        if (panel == null) return;
        setEnabledRecursively(panel, enabled);
        if (gateLabel != null) {
            gateLabel.setVisible(!enabled);
        }
    }

    private static void setEnabledRecursively(Component component, boolean enabled) {
        if (!(component instanceof JLabel)) {
            component.setEnabled(enabled);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                setEnabledRecursively(child, enabled);
            }
        }
    }

    private static void notifyProgressChanged() {
        if (onProgressChanged != null) {
            onProgressChanged.run();
        }
    }
}
