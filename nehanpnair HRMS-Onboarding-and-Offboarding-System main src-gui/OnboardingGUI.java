package gui;

import data.data.*;
import model.model.*;
import service.*;
import proxy.*;
import strategy.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

/**
 * Unified Onboarding & Offboarding Interface.
 * Onboarding: Full feature set
 * Offboarding: Link to teammate's MainGUI
 */
public class OnboardingGUI {

    // ── Onboarding Services ────────────────────────────────────────────────────
    private static IEmployeeProfileData employeeData;
    private static EmployeeService empService;
    private static AssetService assetService;
    private static AccountService accountService;
    private static TrainingService trainingService;
    private static RoleAccessProxy accessControl;
    static List<Employee> onboardingEmployees = new ArrayList<>();
    private static Employee selectedEmployee;

    // ── Current view state ─────────────────────────────────────────────────────
    private static int currentView = 1; // 0: Pre-Onboarding, 1: Onboarding, 2: Offboarding
    private static OnboardingProgressState progressState = new OnboardingProgressState();
    private static PreOnboardingProgressState preOnboardingProgressState = new PreOnboardingProgressState();
    
    // ── Root content area ─────────────────────────────────────────────────────
    private static JPanel contentArea;
    private static JLabel breadcrumbLabel;
    private static JLabel topStatusLabel;
    private static JPanel modulesPanel;
    private static JPanel employeeDetailsPanel;
    private static JPanel progressTrackerPanel;
    private static JPanel preOnboardingNavItem;
    private static JPanel onboardingNavItem;
    private static JPanel offboardingNavItem;
    public static void main(String[] args) {
        setupServices();
        loadEmployeesFromDB();
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        applyUIDefaults();
        SwingUtilities.invokeLater(OnboardingGUI::buildAndShow);
    }

    // ── Global UI defaults ────────────────────────────────────────────────────
    private static void applyUIDefaults() {
        UIManager.put("ComboBox.background",           BG_SURFACE);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",  new Color(0x2E2E36));
        UIManager.put("ComboBox.selectionForeground",  TEXT_PRIMARY);
        UIManager.put("ComboBox.buttonBackground",     BG_SURFACE);
        UIManager.put("Button.background",             BG_SURFACE);
        UIManager.put("Button.foreground",             TEXT_PRIMARY);
        UIManager.put("Button.focus",                  new Color(0, 0, 0, 0));
        UIManager.put("ScrollBar.background",          BG_SURFACE);
        UIManager.put("ScrollBar.thumb",               new Color(0x3A3A3E));
        UIManager.put("ScrollBar.track",               BG_SURFACE);
        UIManager.put("TextField.background",          BG_SURFACE);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     TEXT_PRIMARY);
        UIManager.put("TextField.selectionBackground", new Color(0x3A3A60));
        UIManager.put("List.background",               BG_SURFACE);
        UIManager.put("List.foreground",               TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",      new Color(0x2E2E36));
    }

    // ── DB loading ────────────────────────────────────────────────────────────
    private static void loadEmployeesFromDB() {
        onboardingEmployees.clear();
        try {
            List<Employee> allEmps = employeeData.getAllEmployees();
            if (allEmps != null) {
                onboardingEmployees.addAll(allEmps);
                if (!onboardingEmployees.isEmpty()) {
                    selectedEmployee = onboardingEmployees.get(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: could not load employees: " + e.getMessage());
        }
    }

    // ── Service wiring ────────────────────────────────────────────────────────
    private static void setupServices() {
        employeeData = new EmployeeProfileDataImpl();
        empService = new EmployeeService(employeeData);
        assetService = new AssetService(new AssetDataImpl());
        accountService = new AccountService(new UserAccountDataImpl());
        trainingService = new TrainingService();
        accessControl = new RoleAccessProxy();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Window
    // ═════════════════════════════════════════════════════════════════════════
    private static void buildAndShow() {
        JFrame frame = new JFrame("HRMS — Employee Lifecycle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(1000, 680));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        root.add(buildUnifiedSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG_APP);
        mainArea.add(buildTopBar(), BorderLayout.NORTH);

        // Content area with employee details
        JPanel contentWithDetails = new JPanel(new BorderLayout());
        contentWithDetails.setBackground(BG_APP);
        
        employeeDetailsPanel = buildEmployeeDetailsPanel();
        contentWithDetails.add(employeeDetailsPanel, BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_APP);
        contentWithDetails.add(contentArea, BorderLayout.CENTER);

        mainArea.add(contentWithDetails, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        showOnboardingOverview();
        frame.setVisible(true);
    }

    // ── Top bar ────────────────────────────────────────────────────────────────
    private static JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        top.setBackground(BG_SURFACE);
        top.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, BORDER));

        breadcrumbLabel = label("Dashboard / Onboarding", new Font("Segoe UI", Font.BOLD, 11), TEXT_SECONDARY);
        top.add(breadcrumbLabel);

        top.add(Box.createHorizontalGlue());

        topStatusLabel = label("Ready", FONT_SMALL, TEXT_MUTED);
        top.add(topStatusLabel);

        return top;
    }

    // ── Employee Details Panel ─────────────────────────────────────────────────
    private static JPanel buildEmployeeDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SURFACE);
        panel.setBorder(new javax.swing.border.EmptyBorder(12, 24, 12, 24));

        if (selectedEmployee != null) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 4));
            row.setBackground(BG_SURFACE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row.setOpaque(false);

            JLabel detailsLabel = label(
                "ID: " + selectedEmployee.getEmployeeID() + "  |  " +
                "Name: " + selectedEmployee.getName() + "  |  " +
                "Dept: " + selectedEmployee.getDepartment() + "  |  " +
                "Status: " + selectedEmployee.getStatus(),
                FONT_SMALL, TEXT_SECONDARY);
            row.add(detailsLabel);
            panel.add(row);
        }

        return panel;
    }

    // ── Update Employee Details Display ────────────────────────────────────────
    private static void updateEmployeeDetails() {
        if (employeeDetailsPanel != null) {
            employeeDetailsPanel.removeAll();
            if (selectedEmployee != null) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 4));
                row.setBackground(BG_SURFACE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                row.setOpaque(false);

                JLabel detailsLabel = label(
                    "ID: " + selectedEmployee.getEmployeeID() + "  |  " +
                    "Name: " + selectedEmployee.getName() + "  |  " +
                    "Dept: " + selectedEmployee.getDepartment() + "  |  " +
                    "Status: " + selectedEmployee.getStatus(),
                    FONT_SMALL, TEXT_SECONDARY);
                row.add(detailsLabel);
                employeeDetailsPanel.add(row);
            }
            employeeDetailsPanel.revalidate();
            employeeDetailsPanel.repaint();
        }
    }

    // ── Unified Sidebar ────────────────────────────────────────────────────────
    private static JPanel buildUnifiedSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(270, 0));
        sb.setBorder(new javax.swing.border.MatteBorder(0, 0, 0, 1, BORDER));

        // Brand
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(BG_SIDEBAR);
        brand.setBorder(new javax.swing.border.EmptyBorder(22, 18, 18, 18));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel appLabel = label("HRMS", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        JLabel verLabel = label("v2.1.0", FONT_LABEL, TEXT_MUTED);
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        verLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        brand.add(appLabel);
        brand.add(Box.createVerticalStrut(2));
        brand.add(verLabel);
        sb.add(brand);
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // DASHBOARD section
        JPanel dashH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        dashH.setBackground(BG_SIDEBAR);
        dashH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        dashH.add(label("DASHBOARD", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        sb.add(dashH);
        preOnboardingNavItem = createSubNavItem("  Pre-Onboarding", false, () -> selectPreOnboarding(sb));
        onboardingNavItem = createSubNavItem("  Onboarding",  true,  () -> selectOnboarding(sb));
        offboardingNavItem = createSubNavItem("  Offboarding", false, () -> selectOffboarding(sb));
        sb.add(preOnboardingNavItem);
        sb.add(onboardingNavItem);
        sb.add(offboardingNavItem);
        sb.add(Box.createVerticalStrut(8));
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // Progress Tracker (will switch between pre-onboarding and onboarding progress)
        progressTrackerPanel = OnboardingProgressTracker.build(progressState);
        sb.add(progressTrackerPanel);
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // Modules panel (will be populated dynamically)
        modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        modulesPanel.setBackground(BG_SIDEBAR);
        modulesPanel.setOpaque(false);
        
        // Add onboarding modules by default
        addOnboardingModules(modulesPanel);
        sb.add(modulesPanel);

        sb.add(Box.createVerticalGlue());
        return sb;
    }
    
    private static void addOnboardingModules(JPanel panel) {
        panel.removeAll();
        
        // MODULES section header
        JPanel modH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        modH.setBackground(BG_SIDEBAR);
        modH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        modH.add(label("MODULES", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        panel.add(modH);

        // Flow control: each step enabled only after prerequisites
        panel.add(createModuleButton("Role Assignment",     true, () -> showRoleAssignment()));
        panel.add(createModuleButton("Workflow",            progressState.isRoleAssigned(), () -> showWorkflow()));
        panel.add(createModuleButton("Forms",               progressState.isWorkflowStarted(), () -> showForms()));
        panel.add(createModuleButton("Assets",              progressState.isFormsLoaded(), () -> showAssets()));
        panel.add(createModuleButton("Account",             progressState.isAssetsAllocated(), () -> showAccount()));
        panel.add(createModuleButton("Training",            progressState.isAccountCreated(), () -> showTraining()));
        panel.add(createModuleButton("Access Control",      progressState.isTrainingAssigned(), () -> showAccessControl()));
    }
    
    private static void addOffboardingModules(JPanel panel) {
        panel.removeAll();
        
        // MODULES section header
        JPanel modH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        modH.setBackground(BG_SIDEBAR);
        modH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        modH.add(label("MODULES", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        panel.add(modH);
    }

    private static void addPreOnboardingModules(JPanel panel) {
        panel.removeAll();
        
        // No modules for pre-onboarding, just show the overview
    }
    
    private static void selectPreOnboarding(JPanel sidebar) {
        currentView = 0;
        updateNavItemColors();
        showPreOnboardingPortal();
        if (modulesPanel != null) {
            addPreOnboardingModules(modulesPanel);
            modulesPanel.revalidate();
            modulesPanel.repaint();
        }
        if (progressTrackerPanel != null) {
            progressTrackerPanel.removeAll();
            progressTrackerPanel.add(PreOnboardingProgressTracker.build(preOnboardingProgressState));
            progressTrackerPanel.revalidate();
            progressTrackerPanel.repaint();
        }
        sidebar.revalidate();
        sidebar.repaint();
    }
    
    private static void selectOnboarding(JPanel sidebar) {
        currentView = 1;
        updateNavItemColors();
        showOnboardingOverview();
        if (modulesPanel != null) {
            addOnboardingModules(modulesPanel);
            modulesPanel.revalidate();
            modulesPanel.repaint();
        }
        if (progressTrackerPanel != null) {
            progressTrackerPanel.removeAll();
            progressTrackerPanel.add(OnboardingProgressTracker.build(progressState));
            progressTrackerPanel.revalidate();
            progressTrackerPanel.repaint();
        }
        sidebar.revalidate();
        sidebar.repaint();
    }
    
    private static void selectOffboarding(JPanel sidebar) {
        currentView = 2;
        updateNavItemColors();
        showOffboardingPortal();
        if (modulesPanel != null) {
            addOffboardingModules(modulesPanel);
            modulesPanel.revalidate();
            modulesPanel.repaint();
        }
        if (progressTrackerPanel != null) {
            progressTrackerPanel.removeAll();
            progressTrackerPanel.revalidate();
            progressTrackerPanel.repaint();
        }
        sidebar.revalidate();
        sidebar.repaint();
    }
    
    private static void updateNavItemColors() {
        if (preOnboardingNavItem != null && onboardingNavItem != null && offboardingNavItem != null) {
            boolean preActive = currentView == 0;
            boolean onActive = currentView == 1;
            boolean offActive = currentView == 2;
            
            preOnboardingNavItem.setBackground(preActive ? new Color(0x22202E) : BG_SIDEBAR);
            preOnboardingNavItem.repaint();
            
            onboardingNavItem.setBackground(onActive ? new Color(0x22202E) : BG_SIDEBAR);
            onboardingNavItem.repaint();
            
            offboardingNavItem.setBackground(offActive ? new Color(0x22202E) : BG_SIDEBAR);
            offboardingNavItem.repaint();
            
            // Update text colors
            updateNavItemLabelColors(preOnboardingNavItem, preActive);
            updateNavItemLabelColors(onboardingNavItem, onActive);
            updateNavItemLabelColors(offboardingNavItem, offActive);
        }
    }
    
    private static void updateNavItemLabelColors(JPanel item, boolean active) {
        for (java.awt.Component c : item.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel)c).setForeground(active ? new Color(0xC8BEFF) : TEXT_SECONDARY);
            }
        }
    }

    // ── Progress state updates ─────────────────────────────────────────────────
    private static void updateProgress() {
        if (progressTrackerPanel != null) {
            progressTrackerPanel.removeAll();
            progressTrackerPanel.add(OnboardingProgressTracker.build(progressState));
            progressTrackerPanel.revalidate();
            progressTrackerPanel.repaint();
        }
        
        // Refresh module buttons with new enabled/disabled state
        if (modulesPanel != null && currentView == 1) {
            addOnboardingModules(modulesPanel);
            modulesPanel.revalidate();
            modulesPanel.repaint();
        }
        
        // Check if all completed
        if (progressState.isAllCompleted()) {
            showCompletionScreen();
        }
    }

    private static JPanel createSubNavItem(String text, boolean active, Runnable action) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getBackground().equals(new Color(0x22202E))) {
                    g.setColor(ACCENT);
                    g.fillRect(0, 6, 3, getHeight() - 12);
                }
            }
        };
        item.setOpaque(true);
        item.setBackground(active ? new Color(0x22202E) : BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = label(text, active ? FONT_BOLD : FONT_UI,
                                 active ? new Color(0xC8BEFF) : TEXT_SECONDARY);
        lbl.setBorder(new javax.swing.border.EmptyBorder(0, 20, 0, 0));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!item.getBackground().equals(new Color(0x22202E))) {
                    item.setBackground(new Color(0x1E1E22));
                }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!item.getBackground().equals(new Color(0x22202E))) {
                    item.setBackground(BG_SIDEBAR);
                }
            }
        });

        return item;
    }

    private static JPanel createModuleButton(String text, boolean enabled, Runnable action) {
        JPanel item = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getBackground().equals(new Color(0x22202E))) {
                    g.setColor(ACCENT);
                    g.fillRect(0, 6, 3, getHeight() - 12);
                }
            }
        };
        item.setOpaque(true);
        item.setBackground(BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        item.setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        JLabel lbl = label(text, FONT_UI, enabled ? TEXT_SECONDARY : new Color(0x4A4A50));
        lbl.setBorder(new javax.swing.border.EmptyBorder(0, 20, 0, 0));
        item.add(lbl, BorderLayout.CENTER);

        if (enabled) {
            item.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    item.setBackground(new Color(0x1E1E22));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    item.setBackground(BG_SIDEBAR);
                }
            });
        }

        return item;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation - Pre-Onboarding Views
    // ═════════════════════════════════════════════════════════════════════════

    private static void showPreOnboardingPortal() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Pre-Onboarding");
        if (topStatusLabel != null) topStatusLabel.setText("Ready");
        employeeDetailsPanel.removeAll();
        swap(PreOnboardingOverview.build(preOnboardingProgressState, OnboardingGUI::updatePreOnboardingProgress));
    }
    
    private static void updatePreOnboardingProgress() {
        // Update progress tracker when task is toggled
        if (progressTrackerPanel != null && currentView == 0) {
            progressTrackerPanel.removeAll();
            progressTrackerPanel.add(PreOnboardingProgressTracker.build(preOnboardingProgressState));
            progressTrackerPanel.revalidate();
            progressTrackerPanel.repaint();
        }
        
        // Refresh the content area to show updated task states
        if (currentView == 0) {
            showPreOnboardingPortal();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation - Onboarding Views
    // ═════════════════════════════════════════════════════════════════════════

    private static void showOnboardingOverview() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding");
        if (topStatusLabel != null) topStatusLabel.setText("Ready");
        updateEmployeeDetails();
        swap(OnboardingOverview.build(onboardingEmployees, selectedEmployee, emp -> {
            selectedEmployee = emp;
            updateEmployeeDetails();
            progressState.reset();
            updateProgress();
        }));
    }

    private static void showRoleAssignment() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Role Assignment");
        swap(OnboardingRolePanel.build(selectedEmployee, empService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showWorkflow() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Workflow");
        swap(OnboardingWorkflowPanel.build(selectedEmployee, empService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showForms() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Forms");
        swap(OnboardingFormsPanel.build(selectedEmployee, empService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showAssets() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Assets");
        swap(OnboardingAssetsPanel.build(selectedEmployee, assetService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showAccount() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Account");
        swap(OnboardingAccountPanel.build(selectedEmployee, accountService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showTraining() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Training");
        swap(OnboardingTrainingPanel.build(selectedEmployee, trainingService, progressState, OnboardingGUI::updateProgress));
    }

    private static void showAccessControl() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Access Control");
        swap(OnboardingAccessPanel.build(selectedEmployee, accessControl, progressState, OnboardingGUI::updateProgress));
    }

    private static void showCompletionScreen() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Completed");
        if (topStatusLabel != null) topStatusLabel.setText("Completed");
        swap(OnboardingCompletionPanel.build(selectedEmployee));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation - Offboarding Portal
    // ═════════════════════════════════════════════════════════════════════════

    private static void showOffboardingPortal() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Offboarding");
        if (topStatusLabel != null) topStatusLabel.setText("Idle");
        MainGUI.setContentArea(contentArea);
        MainGUI.applyUIDefaults();
        swap(buildOffboardingPortal());
    }

    private static JPanel buildOffboardingPortal() {
        return MainGUI.initOffboarding();
    }

    private static void swap(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}

