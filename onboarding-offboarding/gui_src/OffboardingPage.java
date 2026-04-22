package gui;

import model.model.Employee;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class OffboardingPage extends JPanel {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DEEP    = new Color(0x0F1623);
    private static final Color BG_CARD    = new Color(0x1A2235);
    private static final Color BG_HOVER   = new Color(0x212D42);
    private static final Color ACCENT     = new Color(0x4F8EF7);
    private static final Color ACCENT2    = new Color(0x6EC6A0);
    private static final Color WARN       = new Color(0xF5A623);
    private static final Color DANGER     = new Color(0xE05C5C);
    private static final Color TEXT_PRI   = new Color(0xEDF2FF);
    private static final Color TEXT_SEC   = new Color(0x8A9BB8);
    private static final Color TEXT_MUT   = new Color(0x4A5568);
    private static final Color BORDER_COL = new Color(0x263147);

    // ── Status colors ─────────────────────────────────────────────────────────
    private static final Color C_PENDING  = new Color(0xF5A623);
    private static final Color C_INPROG   = new Color(0x4F8EF7);
    private static final Color C_DONE     = new Color(0x6EC6A0);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,   22);
    private static final Font FONT_HEAD   = new Font("Segoe UI", Font.BOLD,   14);
    private static final Font FONT_SUB    = new Font("Segoe UI", Font.BOLD,   12);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN,  12);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN,  11);
    private static final Font FONT_BADGE  = new Font("Segoe UI", Font.BOLD,   10);
    private static final Font FONT_KPI    = new Font("Segoe UI", Font.BOLD,   28);

    // ── Data ──────────────────────────────────────────────────────────────────
    private final OffboardingDataSqliteImpl db;
    private List<Employee> employees;
    private String selectedEmpId = null;
    
    public static OffboardingPage instance;

    // ── UI components ─────────────────────────────────────────────────────────
    private JPanel employeeListPanel;
    private JPanel taskPanel;
    private JLabel detailNameLabel, detailRoleLabel, detailStatusBadge;
    private JPanel taskCardContainer;
    private JLabel taskCountLabel;
    private JProgressBar progressBar;
    private JPanel[] statCards = new JPanel[4];
    private JLabel[] statNumbers = new JLabel[4];

    public OffboardingPage() {
        instance = this;
        db = new OffboardingDataSqliteImpl();
        db.ensureTablesExist();

        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        loadData();
    }

    public static void completeTask(String empId, String taskName) {
        if (instance != null) {
            instance.db.updateTaskStatusByName(empId, taskName, "COMPLETED");
            if (empId.equals(instance.selectedEmpId)) {
                instance.loadTaskCards(empId);
                instance.refreshProgressStrip(empId);
            }
            
            // Check if all tasks are completed to update overall status
            int[] stats = instance.db.getTaskStats(empId);
            if (stats[0] > 0 && stats[0] == stats[3]) {
                UnifiedDatabaseManager.updateEmployeeStatus(empId, "OFFBOARDED");
                String empName = "Employee";
                if (instance.employees != null) {
                    for (Employee e : instance.employees) {
                        if (e.getEmployeeID().equals(empId)) {
                            empName = e.getName();
                            break;
                        }
                    }
                }
                JOptionPane.showMessageDialog(instance, "Person " + empName + " successfully offboarded", "Offboarding Complete", JOptionPane.INFORMATION_MESSAGE);
                instance.loadData();
            }
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x162040),
                                                     getWidth(), 0, new Color(0x0F1A35));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_COL);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel breadcrumb = new JLabel("HRMS  ›  Employee Lifecycle  ›  Offboarding");
        breadcrumb.setFont(FONT_SMALL);
        breadcrumb.setForeground(TEXT_MUT);
        breadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("⬡");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        icon.setForeground(DANGER);

        JLabel title = new JLabel("Offboarding");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRI);

        titleRow.add(icon);
        titleRow.add(title);

        left.add(breadcrumb);
        left.add(Box.createVerticalStrut(4));
        left.add(titleRow);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setOpaque(false);

        body.add(buildStatsStrip(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          buildEmployeePanel(),
                                          buildDetailPanel());
        split.setDividerLocation(360);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(BG_DEEP);

        split.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI() {
            @Override public javax.swing.plaf.basic.BasicSplitPaneDivider createDefaultDivider() {
                return new javax.swing.plaf.basic.BasicSplitPaneDivider(this) {
                    { setBackground(BORDER_COL); }
                };
            }
        });

        body.add(split, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildStatsStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 4, 12, 0));
        strip.setOpaque(false);
        strip.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        String[][] statDefs = {
            {"Active Employees", "👥", "#4F8EF7"},
            {"Pending Exit",     "⏳", "#F5A623"},
            {"In Progress",      "🔄", "#4F8EF7"},
            {"Offboarded",       "✅", "#6EC6A0"},
        };

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel card = new JPanel(new BorderLayout(8, 4)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_CARD);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    Color ac = Color.decode(statDefs[idx][2]);
                    g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 40));
                    g2.fill(new RoundRectangle2D.Float(0, getHeight()-4, getWidth(), 4, 0, 0));
                    g2.setColor(ac);
                    g2.fill(new RoundRectangle2D.Float(0, getHeight()-4, getWidth(), 4, 0, 0));
                    g2.dispose();
                }
            };
            card.setOpaque(false);
            card.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

            JLabel iconLbl = new JLabel(statDefs[i][1]);
            iconLbl.setFont(FONT_SMALL);
            iconLbl.setForeground(TEXT_SEC);

            JLabel numLbl = new JLabel("—");
            numLbl.setFont(FONT_KPI);
            numLbl.setForeground(Color.decode(statDefs[i][2]));

            JLabel nameLbl = new JLabel(statDefs[i][0]);
            nameLbl.setFont(FONT_SMALL);
            nameLbl.setForeground(TEXT_SEC);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(nameLbl, BorderLayout.WEST);

            card.add(top,    BorderLayout.NORTH);
            card.add(numLbl, BorderLayout.CENTER);

            statCards[i]   = card;
            statNumbers[i] = numLbl;
            strip.add(card);
        }
        return strip;
    }

    private JPanel buildEmployeePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 24, 20, 8));

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextField search = new JTextField();
        search.setBackground(BG_CARD);
        search.setForeground(TEXT_PRI);
        search.setCaretColor(TEXT_PRI);
        search.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        search.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterEmployees(search.getText().trim().toLowerCase());
            }
        });
        searchRow.add(search, BorderLayout.CENTER);

        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        employeeListPanel.setOpaque(false);
        employeeListPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JScrollPane candidateScroll = new JScrollPane(employeeListPanel);
        candidateScroll.setOpaque(false);
        candidateScroll.getViewport().setOpaque(false);
        candidateScroll.setBorder(null);

        JLabel sectionLabel = new JLabel("ONBOARDED EMPLOYEES");
        sectionLabel.setFont(FONT_BADGE);
        sectionLabel.setForeground(TEXT_MUT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));

        outer.add(sectionLabel,   BorderLayout.NORTH);
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(searchRow,       BorderLayout.NORTH);
        inner.add(candidateScroll, BorderLayout.CENTER);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildDetailPanel() {
        taskPanel = new JPanel(new BorderLayout(0, 0));
        taskPanel.setOpaque(false);
        taskPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 20, 24));
        taskPanel.add(buildEmptyState(), BorderLayout.CENTER);
        return taskPanel;
    }

    private JPanel buildEmptyState() {
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setOpaque(false);
        JLabel lbl = new JLabel("<html><center><font size=5>👤</font><br><br>Select an employee<br>to view offboarding tasks</center></html>");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_MUT);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        empty.add(lbl);
        return empty;
    }

    private void loadData() {
        // Fetch employees who are currently onboarded and working
        employees = UnifiedDatabaseManager.getEmployeesByStatus("ONBOARDED");
        
        // Also fetch OFFBOARDED to show comprehensive stats
        List<Employee> allEmployees = new java.util.ArrayList<>();
        allEmployees.addAll(employees);
        allEmployees.addAll(UnifiedDatabaseManager.getEmployeesByStatus("OFFBOARDED"));
        
        int active = 0, offboarded = 0, inProgress = 0;
        for (Employee e : allEmployees) {
            if ("ONBOARDED".equals(e.getStatus())) active++;
            else if ("OFFBOARDED".equals(e.getStatus())) offboarded++;
        }
        
        statNumbers[0].setText(String.valueOf(active));
        statNumbers[1].setText("0"); // Pending exit
        statNumbers[2].setText(String.valueOf(inProgress));
        statNumbers[3].setText(String.valueOf(offboarded));
        
        for (int i = 0; i < 4; i++) {
            statCards[i].repaint();
        }

        populateEmployeeList(employees);
    }

    private void populateEmployeeList(List<Employee> list) {
        employeeListPanel.removeAll();
        for (Employee e : list) {
            db.seedTasksForEmployee(e.getEmployeeID());
            employeeListPanel.add(buildEmployeeCard(e));
            employeeListPanel.add(Box.createVerticalStrut(6));
        }
        if (list.isEmpty()) {
            JLabel empty = new JLabel("No active employees found");
            empty.setFont(FONT_BODY);
            empty.setForeground(TEXT_MUT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            employeeListPanel.add(empty);
        }
        employeeListPanel.add(Box.createVerticalGlue());
        employeeListPanel.revalidate();
        employeeListPanel.repaint();
    }

    private void filterEmployees(String query) {
        if (query.isEmpty()) {
            populateEmployeeList(employees);
            return;
        }
        List<Employee> filtered = new java.util.ArrayList<>();
        for (Employee e : employees) {
            if (e.getName().toLowerCase().contains(query) ||
                (e.getDepartment() != null && e.getDepartment().toLowerCase().contains(query))) {
                filtered.add(e);
            }
        }
        populateEmployeeList(filtered);
    }

    private void selectEmployee(Employee e) {
        selectedEmpId = e.getEmployeeID();

        taskPanel.removeAll();
        JPanel detail = buildEmployeeDetail(e);
        taskPanel.add(detail, BorderLayout.CENTER);
        loadTaskCards(e.getEmployeeID());
        taskPanel.revalidate();
        taskPanel.repaint();

        for (Component comp : employeeListPanel.getComponents()) {
            if (comp instanceof JPanel p && p.getClientProperty("empId") != null) {
                boolean sel = e.getEmployeeID().equals(p.getClientProperty("empId"));
                p.setBackground(sel ? BG_HOVER : BG_CARD);
                p.repaint();
            }
        }
    }

    private JPanel buildEmployeeDetail(Employee e) {
        JPanel detail = new JPanel(new BorderLayout(0, 12));
        detail.setOpaque(false);

        JPanel infoCard = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        infoCard.setOpaque(false);
        infoCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        nameRow.setOpaque(false);

        JLabel avatar = createAvatar(e.getName());
        detailNameLabel = new JLabel(e.getName());
        detailNameLabel.setFont(FONT_HEAD);
        detailNameLabel.setForeground(TEXT_PRI);

        detailStatusBadge = createStatusBadge(e.getStatus());

        nameRow.add(avatar);
        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(detailNameLabel);
        badgeRow.add(detailStatusBadge);
        nameBlock.add(badgeRow);
        detailRoleLabel = new JLabel((e.getRole() != null ? e.getRole() : "N/A") + " · " + (e.getDepartment() != null ? e.getDepartment() : "N/A"));
        detailRoleLabel.setFont(FONT_SMALL);
        detailRoleLabel.setForeground(TEXT_SEC);
        nameBlock.add(detailRoleLabel);
        nameRow.add(nameBlock);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 8));
        grid.add(makeDetailField("ID", e.getEmployeeID()));
        grid.add(makeDetailField("Department", e.getDepartment() != null ? e.getDepartment() : "N/A"));
        grid.add(makeDetailField("Role", e.getRole() != null ? e.getRole() : "N/A"));
        grid.add(makeDetailField("Email", e.getEmail() != null ? e.getEmail() : "N/A"));

        infoCard.add(nameRow, BorderLayout.NORTH);
        infoCard.add(grid,    BorderLayout.CENTER);

        JPanel progressCard = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        progressCard.setOpaque(false);
        progressCard.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel progHeader = new JPanel(new BorderLayout());
        progHeader.setOpaque(false);
        JLabel progTitle = new JLabel("Offboarding Progress");
        progTitle.setFont(FONT_SUB);
        progTitle.setForeground(TEXT_PRI);
        taskCountLabel = new JLabel("0 / 0 tasks completed");
        taskCountLabel.setFont(FONT_SMALL);
        taskCountLabel.setForeground(TEXT_SEC);
        progHeader.add(progTitle,    BorderLayout.WEST);
        progHeader.add(taskCountLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_HOVER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                if (getValue() > 0) {
                    int w = (int)((getWidth() * getValue()) / (double) getMaximum());
                    GradientPaint gp = new GradientPaint(0, 0, DANGER, w, 0, WARN);
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, getHeight(), getHeight(), getHeight()));
                }
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(0, 8));
        progressBar.setOpaque(false);
        progressBar.setBorderPainted(false);

        progressCard.add(progHeader,  BorderLayout.NORTH);
        progressCard.add(progressBar, BorderLayout.CENTER);
        
        refreshProgressStrip(e.getEmployeeID());

        JPanel taskSection = new JPanel(new BorderLayout(0, 8));
        taskSection.setOpaque(false);

        JPanel taskHeader = new JPanel(new BorderLayout());
        taskHeader.setOpaque(false);
        JLabel taskTitle = new JLabel("OFFBOARDING TASKS");
        taskTitle.setFont(FONT_BADGE);
        taskTitle.setForeground(TEXT_MUT);
        taskHeader.add(taskTitle, BorderLayout.WEST);

        taskCardContainer = new JPanel();
        taskCardContainer.setLayout(new BoxLayout(taskCardContainer, BoxLayout.Y_AXIS));
        taskCardContainer.setOpaque(false);

        JScrollPane taskScroll = new JScrollPane(taskCardContainer);
        taskScroll.setOpaque(false);
        taskScroll.getViewport().setOpaque(false);
        taskScroll.setBorder(null);

        taskSection.add(taskHeader, BorderLayout.NORTH);
        taskSection.add(taskScroll, BorderLayout.CENTER);

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);

        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        progressCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        stack.add(infoCard);
        stack.add(Box.createVerticalStrut(10));
        stack.add(progressCard);
        stack.add(Box.createVerticalStrut(10));
        stack.add(taskSection);

        detail.add(stack, BorderLayout.CENTER);
        return detail;
    }

    private void refreshProgressStrip(String empId) {
        if (taskCountLabel == null || progressBar == null) return;
        int[] stats = db.getTaskStats(empId);
        int total = stats[0], done = stats[3];
        int pct = total > 0 ? (done * 100 / total) : 0;
        
        taskCountLabel.setText(done + " / " + total + " tasks completed");
        progressBar.setValue(pct);
        progressBar.repaint();
    }

    private void loadTaskCards(String empId) {
        taskCardContainer.removeAll();
        List<String[]> tasks = db.getTasksByEmployee(empId);
        for (String[] t : tasks) {
            taskCardContainer.add(buildTaskCard(t, empId));
            taskCardContainer.add(Box.createVerticalStrut(6));
        }
        taskCardContainer.revalidate();
        taskCardContainer.repaint();
    }

    private JPanel buildEmployeeCard(Employee e) {
        JPanel card = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = e.getEmployeeID().equals(selectedEmpId);
                g2.setColor(sel ? BG_HOVER : BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                if (sel) {
                    g2.setColor(DANGER);
                    g2.fill(new RoundRectangle2D.Float(0, 0, 3, getHeight(), 3, 3));
                }
                g2.dispose();
            }
        };
        card.putClientProperty("empId", e.getEmployeeID());
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel av = createAvatar(e.getName());

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(e.getName());
        name.setFont(FONT_SUB);
        name.setForeground(TEXT_PRI);
        JLabel dept = new JLabel((e.getDepartment() != null ? e.getDepartment() : "N/A"));
        dept.setFont(FONT_SMALL);
        dept.setForeground(TEXT_SEC);
        info.add(name);
        info.add(dept);

        card.add(av,    BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent me) { card.repaint(); }
            @Override public void mouseExited(MouseEvent me)  { card.repaint(); }
            @Override public void mouseClicked(MouseEvent me) { selectEmployee(e); }
        });
        return card;
    }

    private JPanel buildTaskCard(String[] t, String empId) {
        String status = t[5];
        Color statusColor = status.equals("COMPLETED") ? C_DONE : (status.equals("IN_PROGRESS") ? C_INPROG : C_PENDING);

        JPanel card = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(statusColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, 3, getHeight(), 3, 3));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        JLabel check = new JLabel(status.equals("COMPLETED") ? "✓" : "○");
        check.setFont(new Font("Segoe UI", Font.BOLD, 16));
        check.setForeground(statusColor);
        check.setPreferredSize(new Dimension(24, 24));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JLabel tName = new JLabel(t[2]);
        tName.setFont(FONT_SUB);
        tName.setForeground(status.equals("COMPLETED") ? TEXT_MUT : TEXT_PRI);
        JLabel tDesc = new JLabel(t[3]);
        tDesc.setFont(FONT_SMALL);
        tDesc.setForeground(TEXT_MUT);
        center.add(tName);
        center.add(tDesc);

        JButton actionBtn = new JButton("Execute");
        actionBtn.setFont(FONT_BADGE);
        actionBtn.setForeground(statusColor);
        actionBtn.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 25));
        actionBtn.setFocusPainted(false);
        if (status.equals("COMPLETED")) {
            actionBtn.setText("Completed");
            actionBtn.setEnabled(false);
        } else {
            actionBtn.addActionListener(e -> {
                EmployeeRecord empRec = new EmployeeRecord(empId, "Employee", "Role", "Dept", "Pending", "Resignation");
                switch (t[2]) {
                    case "Exit Interview":
                        ExitInterviewDialog.show(empRec);
                        break;
                    case "Asset Clearance":
                        ClearanceScreen.show(empRec);
                        break;
                    case "Knowledge Transfer":
                        KnowledgeTransferDialog.show(empRec);
                        break;
                    case "Final Settlement":
                        SettlementScreen.show(empRec);
                        break;
                    case "Document Generation":
                        DocumentScreen.show(empRec);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Task action not defined.");
                }
            });
        }

        card.add(check,  BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.EAST);

        return card;
    }

    private JLabel createAvatar(String name) {
        String init = name != null && name.length() > 0 ? name.substring(0, 1).toUpperCase() : "?";
        JLabel av = new JLabel(init) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x2A364F));
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        av.setFont(new Font("Segoe UI", Font.BOLD, 14));
        av.setForeground(TEXT_PRI);
        av.setHorizontalAlignment(SwingConstants.CENTER);
        av.setPreferredSize(new Dimension(38, 38));
        return av;
    }

    private JLabel createStatusBadge(String status) {
        Color c = status.equals("OFFBOARDED") ? C_DONE : (status.equals("ONBOARDED") ? C_INPROG : C_PENDING);
        JLabel b = new JLabel(" " + status + " ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BADGE);
        b.setForeground(c);
        return b;
    }

    private JPanel makeDetailField(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_SEC);
        JLabel v = new JLabel(value);
        v.setFont(FONT_BODY);
        v.setForeground(TEXT_PRI);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch(Exception e){}
        JFrame f = new JFrame("HRMS - Offboarding");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1000, 650);
        f.setContentPane(new OffboardingPage());
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
