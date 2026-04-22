package gui;

import offboarding.*;
import notification.*;
import progress.*;
import integration.*;

import javax.swing.*;

import data.*;
import data_impl_sqlite.ClearanceDataSqliteImpl;
import data_impl_sqlite.EmployeeProfileDataSqliteImpl;
import data_impl_sqlite.ExitDataInMemoryImpl;
import data_impl_sqlite.ExitInterviewDataSqliteImpl;
import data_impl_sqlite.LeaveDataSqliteImpl;
import data_impl_sqlite.PayrollDataSqliteImpl;
import data_impl_sqlite.TimeTrackingDataSqliteImpl;
import model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static gui.DetailViewPanel.*;
import static gui.PipelineConfig.*;
import static gui.Theme.*;

public class MainGUI {

    // ── Services ──────────────────────────────────────────────────────────────
    private static OffboardingService    offboardingService;
    private static IExitData             exitData;
    private static IEmployeeProfileData  employeeData;
    static         IDocumentData         documentData;
    private static CustomizationFacade customization;
    private static ExitInterviewManager interviewManager;

    // ── Application state ─────────────────────────────────────────────────────
    static final List<EmployeeRecord> employees = new ArrayList<>();

    // ── Root content area (swapped on navigation) ─────────────────────────────
    private static JPanel contentArea;

    // ── Global spinner (animates RUNNING StepIcons) ───────────────────────────
    private static Timer spinnerTimer;

    public static void startSpinner() {
        if (spinnerTimer != null && spinnerTimer.isRunning()) return;
        spinnerTimer = new Timer(100, e -> {
            for (StepIcon ic : stepIcons.values())
                if (ic.state == StepState.RUNNING) ic.tick();
        });
        spinnerTimer.start();
    }

    public static void stopSpinner() {
        if (spinnerTimer != null) spinnerTimer.stop();
    }

    private static ExitType mapExitType(String value) {
    if (value == null) return null;

    switch (value.toLowerCase()) {
        case "resignation":
            return ExitType.RESIGNATION;

        case "termination":
            return ExitType.TERMINATION;

        case "retirement":
            return ExitType.VRS;

        case "contract end":
            return ExitType.LAYOFF;

        default:
            throw new IllegalArgumentException("Unknown exit type: " + value);
    }
}

    // ═════════════════════════════════════════════════════════════════════════
    // Entry point
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        setupServices();
        loadEmployeesFromDB();
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        applyUIDefaults();
        SwingUtilities.invokeLater(MainGUI::buildAndShow);
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
        employees.clear();
        try {
            List<String> allIds = employeeData.getAllEmployees()
                    .stream().map(Employee::getEmployeeID).toList();
            if (allIds == null) return;

            for (String id : allIds) {
                try {
                    ExitRequest req = exitData.getExitDetails(id);
                    if (req == null) continue;

                    EmployeeRecord rec = EmployeeRecord.fromExitRequest(req);
                    employees.add(rec);
                } catch (Exception ignored) {
                    // Employee has no exit record — not in pipeline, skip
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: could not load employees from DB: " + e.getMessage());
        }
    }

    // ── Service wiring ────────────────────────────────────────────────────────
    private static void setupServices() {
        IExitData exitDataRef = new ExitDataInMemoryImpl();
        IExitInterviewData interviewData = new ExitInterviewDataSqliteImpl();
        IClearanceData clearanceData = new ClearanceDataSqliteImpl();
        IPayrollData payrollData = new PayrollDataSqliteImpl();
        ILeaveData leaveData = new LeaveDataSqliteImpl();
        ITimeTrackingData attendanceData = new TimeTrackingDataSqliteImpl();
        IEmployeeProfileData employeeDataRef = new EmployeeProfileDataSqliteImpl();

        DummyData dummy = new DummyData();
        IAssetData assetData = dummy;
        IUserAccountData userData = dummy;
        documentData = dummy;

        employeeData = employeeDataRef;

        ExitManager             exitManager      = new ExitManager(exitDataRef);
        interviewManager = new ExitInterviewManager(interviewData);
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

    public static CustomizationFacade getCustomization() {
        return customization;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Window
    // ═════════════════════════════════════════════════════════════════════════
    private static void buildAndShow() {
        JFrame frame = new JFrame("HRMS — Offboarding");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1040, 720);
        frame.setMinimumSize(new Dimension(860, 580));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);
        root.add(SidebarPanel.build(), BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BG_APP);
        mainArea.add(TopBarPanel.build(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(BG_APP);
        mainArea.add(contentArea, BorderLayout.CENTER);

        root.add(mainArea, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        showListView();
        frame.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Navigation
    // ═════════════════════════════════════════════════════════════════════════
    public static void showListView() {
        if (TopBarPanel.breadcrumbLabel != null)
            TopBarPanel.breadcrumbLabel.setText("Dashboard  /  Offboarding");
        setTopStatus("Idle", TEXT_MUTED, TEXT_SECONDARY);
        swap(ListViewPanel.build(employees, MainGUI::showNewView));
    }

    public static void showDetailView(EmployeeRecord emp) {
        if (TopBarPanel.breadcrumbLabel != null)
            TopBarPanel.breadcrumbLabel.setText("Dashboard  /  Offboarding  /  " + emp.name);
        setTopStatus(emp.overallStatus, emp.statusColor(), emp.statusColor());
        swap(DetailViewPanel.build(emp));
    }

    public static void showNewView() {
        if (TopBarPanel.breadcrumbLabel != null)
            TopBarPanel.breadcrumbLabel.setText("Dashboard  /  Offboarding  /  New");
        setTopStatus("Idle", TEXT_MUTED, TEXT_SECONDARY);
        swap(NewEmployeeView.build());
    }

    private static void swap(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── Top-bar status ────────────────────────────────────────────────────────
    private static void setTopStatus(String text, Color dotColor, Color textColor) {
        SwingUtilities.invokeLater(() -> {
            if (TopBarPanel.topDot != null) {
                TopBarPanel.topDot.setBackground(dotColor);
                TopBarPanel.topDot.repaint();
            }
            if (TopBarPanel.topStatusLabel != null) {
                TopBarPanel.topStatusLabel.setText(text);
                TopBarPanel.topStatusLabel.setForeground(textColor);
            }
        });
    }

    // ═════════════════════════════════════════════════════════════════════════
    // New-employee form submission
    // ═════════════════════════════════════════════════════════════════════════
    public static void submitNewEmployee() {
        String empId = NewEmployeeView.empIdField.getText().trim();
        String name  = NewEmployeeView.nameField.getText().trim();
        String role  = NewEmployeeView.roleField.getText().trim();
        String dept  = (String) NewEmployeeView.deptBox.getSelectedItem();

        java.util.Date date =
            (java.util.Date) NewEmployeeView.lastDayPicker.getModel().getValue();

        String lastDay = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);

        String exitTypeStr = (String) NewEmployeeView.exitTypeBox.getSelectedItem();
        ExitType exitType = mapExitType(exitTypeStr);

        if (empId.isEmpty() || name.isEmpty() || role.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Employee ID, Full Name, Role and Department are required.",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ 1. CREATE EXIT REQUEST (THIS WAS MISSING)
        ExitRequest req = new ExitRequest(
            empId,
            exitType,
            "INITIATED",
            lastDay
        );

        exitData.createExitRequest(req);   // 🔥 THIS FIXES YOUR ISSUE

        // ✅ 2. Update UI
        EmployeeRecord emp = EmployeeRecord.fromExitRequest(req);
        employees.add(0, emp);

        showDetailView(emp);

        appendDetailLog(emp,
            "Employee registered. Click 'Proceed: Exit registration' to begin.", BLUE);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Step execution
    // ═════════════════════════════════════════════════════════════════════════

    /** Runs exactly one pipeline step on a background SwingWorker thread. */
    public static void runSingleStep(EmployeeRecord emp, String key) {
        if (detailProceedBtn != null) detailProceedBtn.setEnabled(false);
        if (detailResetBtn   != null) detailResetBtn.setEnabled(false);

        emp.overallStatus = "Running";
        setTopStatus("Running", AMBER, AMBER);
        applyStepState(key, StepState.RUNNING, emp);
        startSpinner();
        appendDetailLog(emp, STEP_NAMES.get(key) + " — started", TEXT_SECONDARY);

        new SwingWorker<StepState, Void>() {
            String errorMsg = null;

            @Override
            protected StepState doInBackground() {
                try {
                    IWorkflowIntegration wf =
                            getCustomization().getWorkflowIntegration();

                    emp.workflowInstanceId = emp.empId.hashCode();

                    String status = wf.getWorkflowStatus(emp.workflowInstanceId);

                    appendDetailLog(emp, "Workflow status: " + status, TEXT_SECONDARY);

                    java.util.List<IWorkflowIntegration.WorkflowStepInfo> steps =
                            wf.getWorkflowSteps(emp.workflowInstanceId);

                    for (IWorkflowIntegration.WorkflowStepInfo step : steps) {
                        appendDetailLog(emp,
                            step.stepName + " → " + step.status,
                            TEXT_MUTED);
                    }

                    return StepState.DONE;

                } catch (CustomizationException e) {
                    errorMsg = e.getMessage();
                    return StepState.ERROR;
                }
            }

            @Override
            protected void done() {
                stopSpinner();
                StepState result;
                try   { result = get(); }
                catch (Exception ex) { result = StepState.ERROR; errorMsg = ex.getMessage(); }

                applyStepState(key, result, emp);

                if (result == StepState.DONE) {
                    emp.doneCount++;
                    appendDetailLog(emp, STEP_NAMES.get(key) + " — complete", GREEN);
                } else if (result == StepState.ERROR) {
                    emp.errorCount++;
                    appendDetailLog(emp, STEP_NAMES.get(key) + " — error: " + errorMsg, RED);
                } else if (result == StepState.AWAITING) {
                    emp.awaitCount++;
                    appendDetailLog(emp, STEP_NAMES.get(key) + " — awaiting: " + errorMsg, BLUE);
                }

                boolean allDone     = emp.stepStates.values().stream()
                        .allMatch(s -> s == StepState.DONE);
                boolean anyError    = emp.stepStates.values().stream()
                        .anyMatch(s -> s == StepState.ERROR);
                boolean anyAwaiting = emp.stepStates.values().stream()
                        .anyMatch(s -> s == StepState.AWAITING);

                if (allDone) {
                    emp.overallStatus = "Complete";
                    setTopStatus("Complete", GREEN, GREEN);
                    appendDetailLog(emp, "Offboarding completed successfully.", GREEN);
                } else if (anyError) {
                    emp.overallStatus = "Error";
                    setTopStatus("Error", RED, RED);
                    appendDetailLog(emp, "Step failed. Fix the issue and retry.", AMBER);
                } else if (anyAwaiting) {
                    emp.overallStatus = "Awaiting";
                    setTopStatus("Awaiting", BLUE, BLUE);
                    appendDetailLog(emp, "Step awaiting external action.", AMBER);
                } else {
                    emp.overallStatus = "Idle";
                    setTopStatus("In progress", AMBER, AMBER);
                }

                updateInsights(emp);
                refreshProceedButton(emp);
                if (detailResetBtn != null) detailResetBtn.setEnabled(true);
            }
        }.execute();
    }

    public static void proceedNextStep(EmployeeRecord emp) {

        String nextKey = emp.nextPendingKey();

        if (nextKey == null) {
            JOptionPane.showMessageDialog(null, "All steps completed.");
            return;
        }

        switch (nextKey) {

            case "exit":
                runSingleStep(emp, "exit");
                break;

            case "interview":
                ExitInterviewDialog.show(emp, interviewManager);
                break;

            case "clearance":
                ClearanceScreen.show(emp, exitData.getExitDetails(emp.empId));
                break;

            case "knowledge":
                KnowledgeTransferDialog.show(emp, documentData);
                break;

            case "settlement":
                SettlementScreen.show(emp, offboardingService);
                break;

            case "docs":
                DocumentScreen.show(emp, documentData);
                break;

            case "notify":
                runSingleStep(emp, "notify");
                break;

            default:
                JOptionPane.showMessageDialog(null, "Unknown step: " + nextKey);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Pipeline reset
    // ═════════════════════════════════════════════════════════════════════════
    public static void resetEmployeePipeline(EmployeeRecord emp) {
        stopSpinner();
        emp.doneCount  = 0;
        emp.awaitCount = 0;
        emp.errorCount = 0;
        emp.overallStatus          = "Idle";
        emp.interviewDataCollected = false;
        for (String key : STEP_KEYS) applyStepState(key, StepState.PENDING, emp);
        updateInsights(emp);
        if (detailProgressBar != null) detailProgressBar.setProgress(0f, GREEN);
        if (detailPct         != null) detailPct.setText("0%");
        if (detailStage       != null) {
            detailStage.setText("Not started");
            detailStage.setForeground(TEXT_MUTED);
        }
        if (detailLogPane != null) detailLogPane.setText("");
        emp.log.clear();
        refreshProceedButton(emp);
        setTopStatus("Idle", TEXT_MUTED, TEXT_SECONDARY);
    }
}
