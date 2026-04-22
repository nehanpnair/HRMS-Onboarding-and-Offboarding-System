package gui;

import model.model.Candidate;
import model.model.Employee;
import service.*;
import proxy.*;
import strategy.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static gui.Theme.*;
import static gui.UIFactory.*;

public class OnboardingGUI {

    private static EmployeeService empService;
    private static AssetService assetService;
    private static AccountService accountService;
    private static TrainingService trainingService;
    private static RoleAccessProxy accessControl;

    private static PreOnboardingService preOnboardingService;
    private static int currentView = 1; // 0: Pre, 1: On, 2: Off
    
    private static OnboardingProgressState progressState = new OnboardingProgressState();
    private static PreOnboardingProgressState preOnboardingProgressState;
    private static Employee selectedEmployee;

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
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) {}
        applyUIDefaults();
        SwingUtilities.invokeLater(OnboardingGUI::buildAndShow);
    }

    private static void applyUIDefaults() {
        UIManager.put("ComboBox.background", BG_SURFACE);
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", new Color(0x2E2E36));
        UIManager.put("ComboBox.selectionForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", BG_SURFACE);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Button.focus", new Color(0,0,0,0));
        UIManager.put("TextField.background", BG_SURFACE);
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
        UIManager.put("List.background", BG_SURFACE);
        UIManager.put("List.foreground", TEXT_PRIMARY);
        UIManager.put("List.selectionBackground", new Color(0x2E2E36));
    }

    private static void setupServices() {
        empService = new EmployeeService(null);
        assetService = new AssetService(null);
        accountService = new AccountService(null);
        trainingService = new TrainingService();
        accessControl = new RoleAccessProxy();
        preOnboardingService = new PreOnboardingService(null, null, null);
    }

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

        JPanel contentWithDetails = new JPanel(new BorderLayout());
        contentWithDetails.setBackground(BG_APP);
        
        employeeDetailsPanel = new JPanel();
        employeeDetailsPanel.setLayout(new BoxLayout(employeeDetailsPanel, BoxLayout.Y_AXIS));
        employeeDetailsPanel.setBackground(BG_SURFACE);
        employeeDetailsPanel.setBorder(new javax.swing.border.EmptyBorder(12, 24, 12, 24));
        contentWithDetails.add(employeeDetailsPanel, BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_APP);
        contentWithDetails.add(contentArea, BorderLayout.CENTER);

        mainArea.add(contentWithDetails, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        
        // Start in Pre-Onboarding view
        selectPreOnboarding(null);
        
        frame.setVisible(true);
    }

    private static void refreshOnboardingProgressTracker() {
        progressTrackerPanel.removeAll();
        progressTrackerPanel.add(OnboardingProgressTracker.build(progressState));
        progressTrackerPanel.revalidate();
        progressTrackerPanel.repaint();
    }

    private static JPanel buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        top.setBackground(BG_SURFACE);
        top.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, BORDER));
        breadcrumbLabel = label("Dashboard", new Font("Segoe UI", Font.BOLD, 11), TEXT_SECONDARY);
        top.add(breadcrumbLabel);
        top.add(Box.createHorizontalGlue());
        topStatusLabel = label("Ready", FONT_SMALL, TEXT_MUTED);
        top.add(topStatusLabel);
        return top;
    }

    private static void updateEmployeeDetails(String info) {
        employeeDetailsPanel.removeAll();
        if (info != null && !info.isEmpty()) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 4));
            row.setBackground(BG_SURFACE);
            row.setOpaque(false);
            row.add(label(info, FONT_SMALL, TEXT_SECONDARY));
            employeeDetailsPanel.add(row);
        }
        employeeDetailsPanel.revalidate();
        employeeDetailsPanel.repaint();
    }

    private static JPanel buildUnifiedSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(270, 0));
        sb.setBorder(new javax.swing.border.MatteBorder(0, 0, 0, 1, BORDER));

        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(BG_SIDEBAR);
        brand.setBorder(new javax.swing.border.EmptyBorder(22, 18, 18, 18));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel appLabel = label("HRMS", new Font("Segoe UI", Font.BOLD, 16), TEXT_PRIMARY);
        brand.add(appLabel);
        sb.add(brand);
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        JPanel dashH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        dashH.setBackground(BG_SIDEBAR);
        dashH.add(label("DASHBOARD", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        sb.add(dashH);

        preOnboardingNavItem = createSubNavItem("  Pre-Onboarding", false, () -> selectPreOnboarding(sb));
        onboardingNavItem = createSubNavItem("  Onboarding", false, () -> selectOnboarding(sb));
        offboardingNavItem = createSubNavItem("  Offboarding", false, () -> selectOffboarding(sb));
        sb.add(preOnboardingNavItem);
        sb.add(onboardingNavItem);
        sb.add(offboardingNavItem);
        sb.add(Box.createVerticalStrut(8));
        sb.add(hRule());

        progressTrackerPanel = new JPanel(new BorderLayout());
        progressTrackerPanel.setBackground(BG_SIDEBAR);
        sb.add(progressTrackerPanel);

        modulesPanel = new JPanel();
        modulesPanel.setLayout(new BoxLayout(modulesPanel, BoxLayout.Y_AXIS));
        modulesPanel.setBackground(BG_SIDEBAR);
        sb.add(modulesPanel);

        sb.add(Box.createVerticalGlue());
        return sb;
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
        item.setBackground(BG_SIDEBAR);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lbl = label(text, FONT_UI, TEXT_SECONDARY);
        lbl.setBorder(new javax.swing.border.EmptyBorder(0, 20, 0, 0));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { if (!item.getBackground().equals(new Color(0x22202E))) item.setBackground(new Color(0x1E1E22)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { if (!item.getBackground().equals(new Color(0x22202E))) item.setBackground(BG_SIDEBAR); }
        });
        return item;
    }

    private static void updateNavItemColors() {
        if (preOnboardingNavItem != null) {
            preOnboardingNavItem.setBackground(currentView == 0 ? new Color(0x22202E) : BG_SIDEBAR);
            onboardingNavItem.setBackground(currentView == 1 ? new Color(0x22202E) : BG_SIDEBAR);
            offboardingNavItem.setBackground(currentView == 2 ? new Color(0x22202E) : BG_SIDEBAR);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 0. Pre-Onboarding
    // ═════════════════════════════════════════════════════════════════════════
    private static void selectPreOnboarding(JPanel sb) {
        currentView = 0;
        updateNavItemColors();
        updateEmployeeDetails("");
        breadcrumbLabel.setText("Dashboard / Pre-Onboarding / Candidates");
        
        modulesPanel.removeAll();
        progressTrackerPanel.removeAll();
        modulesPanel.revalidate(); modulesPanel.repaint();
        progressTrackerPanel.revalidate(); progressTrackerPanel.repaint();

        swap(new PreOnboardingPage());
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 1. Onboarding
    // ═════════════════════════════════════════════════════════════════════════
    private static void selectOnboarding(JPanel sb) {
        currentView = 1;
        updateNavItemColors();
        updateEmployeeDetails("");
        breadcrumbLabel.setText("Dashboard / Onboarding / Employees");

        progressTrackerPanel.removeAll();
        modulesPanel.removeAll();
        progressTrackerPanel.revalidate(); progressTrackerPanel.repaint();

        List<Employee> emps = UnifiedDatabaseManager.getEmployeesByStatus("ONBOARDING_IN_PROGRESS");
        swap(OnboardingOverview.build(emps, null, e -> {
            selectedEmployee = e;
            updateEmployeeDetails("");
            breadcrumbLabel.setText("Dashboard / Onboarding / " + e.getName());
            progressState.reset();
            
            modulesPanel.removeAll();
            modulesPanel.revalidate(); modulesPanel.repaint();
            
            refreshOnboardingProgressTracker();
            
            swap(OnboardingDetailPanel.build(selectedEmployee, progressState, updatedEmp -> {
                selectOnboarding(null);
            }, OnboardingGUI::refreshOnboardingProgressTracker));
        }));
    }

    // Modules are now handled inside OnboardingDetailPanel.java


    // ═════════════════════════════════════════════════════════════════════════
    // 2. Offboarding
    // ═════════════════════════════════════════════════════════════════════════
    private static void selectOffboarding(JPanel sb) {
        currentView = 2;
        updateNavItemColors();
        updateEmployeeDetails("");
        breadcrumbLabel.setText("Dashboard / Offboarding");

        progressTrackerPanel.removeAll();
        modulesPanel.removeAll();
        progressTrackerPanel.revalidate(); progressTrackerPanel.repaint();

        swap(new OffboardingPage());
    }

    private static void swap(Component panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}
