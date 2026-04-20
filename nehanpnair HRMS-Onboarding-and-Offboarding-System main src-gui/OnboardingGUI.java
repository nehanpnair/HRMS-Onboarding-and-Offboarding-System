package gui;

import offboarding.*;
import notification.*;
import progress.*;
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
 * Unified Onboarding & Offboarding GUI.
 * Combined interface for both employee lifecycle modules.
 */
public class OnboardingGUI {

    // ── Current view state ─────────────────────────────────────────────────────
    private static boolean showingOnboarding = true;

    // ── Onboarding Services ────────────────────────────────────────────────────
    private static IEmployeeProfileData employeeData;
    private static EmployeeService empService;
    private static AssetService assetService;
    private static AccountService accountService;
    private static TrainingService trainingService;
    private static RoleAccessProxy accessControl;
    static List<Employee> onboardingEmployees = new ArrayList<>();
    private static Employee selectedEmployee;

    // ── Offboarding Services ───────────────────────────────────────────────────
    private static OffboardingService offboardingService;
    private static IExitData exitData;
    static final List<EmployeeRecord> offboardingEmployees = new ArrayList<>();
    private static CustomizationFacade customization;

    // ── Root content area ─────────────────────────────────────────────────────
    private static JPanel contentArea;
    private static JLabel breadcrumbLabel;
    private static JLabel topStatusLabel;

    // ═════════════════════════════════════════════════════════════════════════
    // Entry point
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        setupOnboardingServices();
        setupOffboardingServices();
        loadOnboardingEmployeesFromDB();
        loadOffboardingEmployeesFromDB();
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

    // ── DB loading ─────────────────────────────────────────────────────────────
    private static void loadOnboardingEmployeesFromDB() {
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
            System.err.println("Warning: could not load onboarding employees: " + e.getMessage());
        }
    }

    private static void loadOffboardingEmployeesFromDB() {
        offboardingEmployees.clear();
        try {
            List<String> allIds = employeeData.getAllEmployees()
                    .stream().map(Employee::getEmployeeID).toList();
            if (allIds == null) return;
            for (String id : allIds) {
                try {
                    ExitRequest req = exitData.getExitDetails(id);
                    if (req == null) continue;
                    EmployeeRecord rec = EmployeeRecord.fromExitRequest(req);
                    offboardingEmployees.add(rec);
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.err.println("Warning: could not load offboarding employees: " + e.getMessage());
        }
    }

    // ── Service wiring ────────────────────────────────────────────────────────
    private static void setupOnboardingServices() {
        employeeData = new EmployeeProfileDataImpl();
        empService = new EmployeeService(employeeData);
        assetService = new AssetService(new AssetDataImpl());
        accountService = new AccountService(new UserAccountDataImpl());
        trainingService = new TrainingService();
        accessControl = new RoleAccessProxy();
    }

    private static void setupOffboardingServices() {
        DummyData dummy = new DummyData();

        IExitData            exitDataRef     = dummy;
        IExitInterviewData   interviewData   = dummy;
        IAssetData           assetData       = dummy;
        IUserAccountData     userData        = dummy;
        IClearanceData       clearanceData   = dummy;
        IPayrollData         payrollData     = dummy;
        ILeaveData           leaveData       = dummy;
        ITimeTrackingData    attendanceData  = dummy;
        IEmployeeProfileData employeeDataRef = dummy;
        IDocumentData        documentData    = dummy;

        IEmployeeProfileData empDataOnboarding = new EmployeeProfileDataImpl();
        employeeData = empDataOnboarding;

        ExitManager             exitManager      = new ExitManager(exitDataRef);
        ExitInterviewManager    interviewManager = new ExitInterviewManager(interviewData);
        RealClearanceService    realClearance    = new RealClearanceService(assetData, userData, clearanceData);
        ClearanceManager        proxyClearance   = new ProxyClearanceService(realClearance);
        SettlementService       settlementSvc    = new SettlementService(
                payrollData, leaveData, attendanceData, assetData, employeeDataRef);
        KnowledgeTransferService ktService       = new KnowledgeTransferService();
        exitData     = exitManager;
        offboardingService = new OffboardingService(
            exitManager, interviewManager, proxyClearance, settlementSvc,
            new DocumentGenerator(), new NotificationService(),
            new ProgressTracker(), ktService);

        customization = new MockCustomizationFacade();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Window
    // ═════════════════════════════════════════════════════════════════════════
    private static void buildAndShow() {
        JFrame frame = new JFrame("HRMS — Employee Lifecycle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1040, 720);
        frame.setMinimumSize(new Dimension(860, 580));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        root.add(buildUnifiedSidebar(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG_APP);
        mainArea.add(buildTopBar(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_APP);
        mainArea.add(contentArea, BorderLayout.CENTER);

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

        topStatusLabel = label("Idle", FONT_SMALL, TEXT_MUTED);
        top.add(topStatusLabel);

        return top;
    }

    // ── Unified Sidebar ────────────────────────────────────────────────────────
    private static JPanel buildUnifiedSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(BG_SIDEBAR);
        sb.setPreferredSize(new Dimension(210, 0));
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
        sb.add(createSubNavItem("  Onboarding",  true,  () -> showOnboardingOverview()));
        sb.add(createSubNavItem("  Offboarding", false, () -> showOffboardingOverview()));
        sb.add(Box.createVerticalStrut(8));
        sb.add(hRule());
        sb.add(Box.createVerticalStrut(10));

        // Onboarding tabs (shown when onboarding is active)
        JPanel onboardingSection = buildOnboardingTabs();
        onboardingSection.setName("onboarding-tabs");
        sb.add(onboardingSection);

        sb.add(Box.createVerticalGlue());
        return sb;
    }

    private static JPanel buildOnboardingTabs() {
        JPanel tabs = new JPanel();
        tabs.setLayout(new BoxLayout(tabs, BoxLayout.Y_AXIS));
        tabs.setBackground(BG_SIDEBAR);
        tabs.setOpaque(false);

        // MODULES section header
        JPanel modH = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        modH.setBackground(BG_SIDEBAR);
        modH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        modH.add(label("MODULES", new Font("Segoe UI", Font.BOLD, 10), TEXT_MUTED));
        tabs.add(modH);

        tabs.add(createTabButton("Role Assignment",     false, () -> showRoleAssignment()));
        tabs.add(createTabButton("Workflow",            false, () -> showWorkflow()));
        tabs.add(createTabButton("Forms",               false, () -> showForms()));
        tabs.add(createTabButton("Assets",              false, () -> showAssets()));
        tabs.add(createTabButton("Account",             false, () -> showAccount()));
        tabs.add(createTabButton("Training",            false, () -> showTraining()));
        tabs.add(createTabButton("Access Control",      false, () -> showAccessControl()));

        return tabs;
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

    private static JPanel createTabButton(String text, boolean active, Runnable action) {
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

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation - Onboarding Views
    // ═════════════════════════════════════════════════════════════════════════

    private static void showOnboardingOverview() {
        showingOnboarding = true;
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding");
        if (topStatusLabel != null) topStatusLabel.setText("Ready");
        swap(OnboardingOverview.build(onboardingEmployees, selectedEmployee, emp -> selectedEmployee = emp));
    }

    private static void showRoleAssignment() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Role Assignment");
        swap(OnboardingRolePanel.build(selectedEmployee, empService));
    }

    private static void showWorkflow() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Workflow");
        swap(OnboardingWorkflowPanel.build(selectedEmployee, empService));
    }

    private static void showForms() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Forms");
        swap(OnboardingFormsPanel.build(selectedEmployee, empService));
    }

    private static void showAssets() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Assets");
        swap(OnboardingAssetsPanel.build(selectedEmployee, assetService));
    }

    private static void showAccount() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Account");
        swap(OnboardingAccountPanel.build(selectedEmployee, accountService));
    }

    private static void showTraining() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Training");
        swap(OnboardingTrainingPanel.build(selectedEmployee, trainingService));
    }

    private static void showAccessControl() {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Onboarding  /  Access Control");
        swap(OnboardingAccessPanel.build(selectedEmployee, accessControl));
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation - Offboarding Views
    // ═════════════════════════════════════════════════════════════════════════

    private static void showOffboardingOverview() {
        showingOnboarding = false;
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Offboarding");
        if (topStatusLabel != null) topStatusLabel.setText("Idle");
        swap(ListViewPanel.build(offboardingEmployees, OnboardingGUI::showOffboardingDetail));
    }

    private static void showOffboardingDetail(EmployeeRecord emp) {
        if (breadcrumbLabel != null) breadcrumbLabel.setText("Dashboard  /  Offboarding  /  " + emp.name);
        if (topStatusLabel != null) {
            topStatusLabel.setText(emp.overallStatus);
            topStatusLabel.setForeground(emp.statusColor());
        }
        swap(DetailViewPanel.build(emp));
    }

    private static void swap(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}

