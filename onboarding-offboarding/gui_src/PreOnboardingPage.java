package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * PreOnboardingPage — Swing GUI for the Pre-Onboarding module.
 *
 * Fetches all data live from hrms.db via PreOnboardingDataSqliteImpl.
 * Drop this file into the project's gui/ package — it touches nothing else.
 *
 * Layout:
 *   ┌─ Header bar ─────────────────────────────────────────────────────┐
 *   ├─ Stats strip (4 KPI cards) ──────────────────────────────────────┤
 *   ├─ Left panel: Candidate list │ Right panel: Task details ──────────┤
 *   └──────────────────────────────────────────────────────────────────┘
 */
public class PreOnboardingPage extends JPanel {

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
    private final PreOnboardingDataSqliteImpl db;
    private List<String[]> candidates;
    private String selectedCandidateId = null;

    // ── UI components ─────────────────────────────────────────────────────────
    private JPanel candidateListPanel;
    private JScrollPane candidateScroll;
    private JPanel taskPanel;
    private JLabel detailNameLabel, detailRoleLabel, detailStatusBadge;
    private JLabel detailEmailLabel, detailPhoneLabel, detailJoinLabel;
    private JPanel taskCardContainer;
    private JLabel taskCountLabel;
    private JProgressBar progressBar;
    private JPanel[] statCards = new JPanel[4];
    private JLabel[] statNumbers = new JLabel[4];

    // ─────────────────────────────────────────────────────────────────────────
    public PreOnboardingPage() {
        db = new PreOnboardingDataSqliteImpl();
        db.ensureTablesExist();

        setLayout(new BorderLayout());
        setBackground(BG_DEEP);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        loadData();
    }

    // ══════════════════════════════ HEADER ════════════════════════════════════
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

        // Left: title + breadcrumb
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel breadcrumb = new JLabel("HRMS  ›  Recruitment  ›  Pre-Onboarding");
        breadcrumb.setFont(FONT_SMALL);
        breadcrumb.setForeground(TEXT_MUT);
        breadcrumb.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("⬡");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 24));
        icon.setForeground(ACCENT);

        JLabel title = new JLabel("Pre-Onboarding");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRI);

        titleRow.add(icon);
        titleRow.add(title);

        left.add(breadcrumb);
        left.add(Box.createVerticalStrut(4));
        left.add(titleRow);

        // Right: Add candidate button
        JButton addBtn = createPrimaryButton("+ Add Candidate");
        addBtn.addActionListener(e -> showAddCandidateDialog());

        header.add(left,   BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    // ══════════════════════════════ BODY ══════════════════════════════════════
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setOpaque(false);

        body.add(buildStatsStrip(), BorderLayout.NORTH);

        // Main content: left candidate list + right detail
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          buildCandidatePanel(),
                                          buildDetailPanel());
        split.setDividerLocation(360);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(BG_DEEP);

        // Style the divider
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

    // ── Stats strip ───────────────────────────────────────────────────────────
    private JPanel buildStatsStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 4, 12, 0));
        strip.setOpaque(false);
        strip.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        String[][] statDefs = {
            {"Total Candidates", "👥", "#4F8EF7"},
            {"Pending",          "⏳", "#F5A623"},
            {"In Progress",      "🔄", "#4F8EF7"},
            {"Completed",        "✅", "#6EC6A0"},
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

            JLabel iconLbl = new JLabel(statDefs[i][0]);
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

    // ── Candidate list panel ──────────────────────────────────────────────────
    private JPanel buildCandidatePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(0, 24, 20, 8));

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextField search = new JTextField();
        styleTextField(search, "🔍  Search candidates…");
        search.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                filterCandidates(search.getText().trim().toLowerCase());
            }
        });
        searchRow.add(search, BorderLayout.CENTER);

        // Candidate cards container
        candidateListPanel = new JPanel();
        candidateListPanel.setLayout(new BoxLayout(candidateListPanel, BoxLayout.Y_AXIS));
        candidateListPanel.setOpaque(false);
        candidateListPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        candidateScroll = new JScrollPane(candidateListPanel);
        candidateScroll.setOpaque(false);
        candidateScroll.getViewport().setOpaque(false);
        candidateScroll.setBorder(null);
        candidateScroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        candidateScroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        JLabel sectionLabel = new JLabel("CANDIDATES");
        sectionLabel.setFont(FONT_BADGE);
        sectionLabel.setForeground(TEXT_MUT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));

        outer.add(sectionLabel,   BorderLayout.NORTH);
        // stack search + list
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(searchRow,       BorderLayout.NORTH);
        inner.add(candidateScroll, BorderLayout.CENTER);
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    // ── Detail / task panel ───────────────────────────────────────────────────
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
        JLabel lbl = new JLabel("<html><center><font size=5>👤</font><br><br>Select a candidate<br>to view their onboarding tasks</center></html>");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_MUT);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        empty.add(lbl);
        return empty;
    }

    private JPanel buildCandidateDetail(String[] c) {
        JPanel detail = new JPanel(new BorderLayout(0, 12));
        detail.setOpaque(false);

        // ── Candidate info card ───────────────────────────────────────────────
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

        // Avatar + name row
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        nameRow.setOpaque(false);

        JLabel avatar = createAvatar(c[2]);
        detailNameLabel = new JLabel(c[2]);
        detailNameLabel.setFont(FONT_HEAD);
        detailNameLabel.setForeground(TEXT_PRI);

        detailStatusBadge = createStatusBadge(c[4]);

        nameRow.add(avatar);
        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(detailNameLabel);
        badgeRow.add(detailStatusBadge);
        nameBlock.add(badgeRow);
        detailRoleLabel = new JLabel(c[3]);
        detailRoleLabel.setFont(FONT_SMALL);
        detailRoleLabel.setForeground(TEXT_SEC);
        nameBlock.add(detailRoleLabel);
        nameRow.add(nameBlock);

        // Change status button
        JButton statusBtn = createSecondaryButton("Change Status ▾");
        statusBtn.addActionListener(e -> showStatusMenu(statusBtn, c[0]));

        JPanel nameStatusRow = new JPanel(new BorderLayout());
        nameStatusRow.setOpaque(false);
        nameStatusRow.add(nameRow,  BorderLayout.WEST);
        nameStatusRow.add(statusBtn, BorderLayout.EAST);

        // Details grid
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 8));
        grid.add(makeDetailField("✉ Email", c[1]));
        grid.add(makeDetailField("Phone", c[5]));
        grid.add(makeDetailField("Skills", c[3]));
        grid.add(makeDetailField("ID", c[0]));
        grid.add(makeDetailField("Status", c[4]));
        grid.add(makeDetailField("Name", c[2]));

        infoCard.add(nameStatusRow, BorderLayout.NORTH);
        infoCard.add(grid,          BorderLayout.CENTER);

        // ── Progress strip ────────────────────────────────────────────────────
        int[] stats = db.getTaskStats(c[0]);
        int total = stats[0], done = stats[3];
        int pct   = total > 0 ? (done * 100 / total) : 0;

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
        JLabel progTitle = new JLabel("Onboarding Progress");
        progTitle.setFont(FONT_SUB);
        progTitle.setForeground(TEXT_PRI);
        taskCountLabel = new JLabel(done + " / " + total + " tasks completed");
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
                    GradientPaint gp = new GradientPaint(0, 0, ACCENT, w, 0, ACCENT2);
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, getHeight(), getHeight(), getHeight()));
                }
                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(0, 8));
        progressBar.setValue(pct);
        progressBar.setOpaque(false);
        progressBar.setBorderPainted(false);

        // Mini stat pills
        JPanel pills = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pills.setOpaque(false);
        pills.add(makeStatPill("Pending",     stats[1], C_PENDING));
        pills.add(makeStatPill("In Progress", stats[2], C_INPROG));
        pills.add(makeStatPill("Completed",   stats[3], C_DONE));

        progressCard.add(progHeader,  BorderLayout.NORTH);
        progressCard.add(progressBar, BorderLayout.CENTER);
        progressCard.add(pills,        BorderLayout.SOUTH);

        // ── Task list ─────────────────────────────────────────────────────────
        JPanel taskSection = new JPanel(new BorderLayout(0, 8));
        taskSection.setOpaque(false);

        JPanel taskHeader = new JPanel(new BorderLayout());
        taskHeader.setOpaque(false);
        JLabel taskTitle = new JLabel("TASKS");
        taskTitle.setFont(FONT_BADGE);
        taskTitle.setForeground(TEXT_MUT);

        JButton addTaskBtn = createSecondaryButton("+ Add Task");
        addTaskBtn.addActionListener(e -> showAddTaskDialog(c[0]));

        taskHeader.add(taskTitle,  BorderLayout.WEST);
        taskHeader.add(addTaskBtn, BorderLayout.EAST);

        taskCardContainer = new JPanel();
        taskCardContainer.setLayout(new BoxLayout(taskCardContainer, BoxLayout.Y_AXIS));
        taskCardContainer.setOpaque(false);

        JScrollPane taskScroll = new JScrollPane(taskCardContainer);
        taskScroll.setOpaque(false);
        taskScroll.getViewport().setOpaque(false);
        taskScroll.setBorder(null);
        taskScroll.getVerticalScrollBar().setUI(new SlimScrollBarUI());
        taskScroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));

        taskSection.add(taskHeader, BorderLayout.NORTH);
        taskSection.add(taskScroll, BorderLayout.CENTER);

        // Stack: info + progress + tasks
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

    // ══════════════════════════════ DATA LOADING ══════════════════════════════
    private void loadData() {
        candidates = db.getAllCandidates();
        refreshStats();
        populateCandidateList(candidates);
    }

    private void refreshStats() {
        int[] s = db.getOverallStats();
        String[] vals = { String.valueOf(s[0]), String.valueOf(s[1]),
                          String.valueOf(s[2]), String.valueOf(s[3]) };
        for (int i = 0; i < 4; i++) {
            statNumbers[i].setText(vals[i]);
            statCards[i].repaint();
        }
    }

    private void populateCandidateList(List<String[]> list) {
        candidateListPanel.removeAll();
        for (String[] c : list) {
            candidateListPanel.add(buildCandidateCard(c));
            candidateListPanel.add(Box.createVerticalStrut(6));
        }
        if (list.isEmpty()) {
            JLabel empty = new JLabel("No candidates found");
            empty.setFont(FONT_BODY);
            empty.setForeground(TEXT_MUT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            candidateListPanel.add(empty);
        }
        candidateListPanel.add(Box.createVerticalGlue());
        candidateListPanel.revalidate();
        candidateListPanel.repaint();
    }

    private void filterCandidates(String query) {
        if (query.isEmpty()) {
            populateCandidateList(candidates);
            return;
        }
        List<String[]> filtered = new java.util.ArrayList<>();
        for (String[] c : candidates) {
            if (c[2].toLowerCase().contains(query) ||
                c[4].toLowerCase().contains(query) ||
                c[5].toLowerCase().contains(query)) {
                filtered.add(c);
            }
        }
        populateCandidateList(filtered);
    }

    private void selectCandidate(String candidateId) {
        selectedCandidateId = candidateId;
        String[] c = db.getCandidateById(candidateId);
        if (c == null) return;

        taskPanel.removeAll();
        JPanel detail = buildCandidateDetail(c);
        taskPanel.add(detail, BorderLayout.CENTER);
        loadTaskCards(candidateId);
        taskPanel.revalidate();
        taskPanel.repaint();

        // Highlight selected card
        for (Component comp : candidateListPanel.getComponents()) {
            if (comp instanceof JPanel p) {
                boolean sel = candidateId.equals(p.getClientProperty("candidateId"));
                p.setBackground(sel ? BG_HOVER : BG_CARD);
                p.repaint();
            }
        }
    }

    private void loadTaskCards(String candidateId) {
        taskCardContainer.removeAll();
        List<String[]> tasks = db.getTasksByCandidate(candidateId);
        for (String[] t : tasks) {
            taskCardContainer.add(buildTaskCard(t));
            taskCardContainer.add(Box.createVerticalStrut(6));
        }
        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("No tasks yet — click \"+ Add Task\" to create one.");
            empty.setFont(FONT_SMALL);
            empty.setForeground(TEXT_MUT);
            taskCardContainer.add(empty);
        }
        taskCardContainer.revalidate();
        taskCardContainer.repaint();
    }

    // ══════════════════════════════ CARD BUILDERS ═════════════════════════════
    private JPanel buildCandidateCard(String[] c) {
        // c = [id, email, name, skills, status, phone]
        JPanel card = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean sel = c[0].equals(selectedCandidateId);
                g2.setColor(sel ? BG_HOVER : BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                if (sel) {
                    g2.setColor(ACCENT);
                    g2.fill(new RoundRectangle2D.Float(0, 0, 3, getHeight(), 3, 3));
                }
                g2.dispose();
            }
        };
        card.putClientProperty("candidateId", c[0]);
        card.setOpaque(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        card.setPreferredSize(new Dimension(300, 72));

        // Avatar
        JLabel av = createAvatar(c[2]);

        // Name + role
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(c[2]);
        name.setFont(FONT_SUB);
        name.setForeground(TEXT_PRI);
        JLabel role = new JLabel(c[3]);
        role.setFont(FONT_SMALL);
        role.setForeground(TEXT_SEC);
        info.add(name);
        info.add(role);

        // Status badge + join date
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel badge = createStatusBadge(c[4]);
        badge.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel join = new JLabel(""); // no join date in DB
        join.setFont(FONT_SMALL);
        join.setForeground(TEXT_MUT);
        join.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(badge);
        right.add(Box.createVerticalStrut(3));
        right.add(join);

        card.add(av,    BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        // Hover + click
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { card.repaint(); }
            @Override public void mouseExited(MouseEvent e)  { card.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { selectCandidate(c[0]); }
        });
        return card;
    }

    private JPanel buildTaskCard(String[] t) {
        // t = [taskId, candidateId, name, description, category, status, dueDate, completedDate]
        String status = t[5];
        Color statusColor = switch (status) {
            case "COMPLETED"   -> C_DONE;
            case "IN_PROGRESS" -> C_INPROG;
            default            -> C_PENDING;
        };

        JPanel card = new JPanel(new BorderLayout(10, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // left accent bar by category
                g2.setColor(statusColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, 3, getHeight(), 3, 3));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setPreferredSize(new Dimension(0, 80));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        // Check circle
        JLabel check = new JLabel(status.equals("COMPLETED") ? "✓" : "○");
        check.setFont(new Font("Segoe UI", Font.BOLD, 16));
        check.setForeground(statusColor);
        check.setPreferredSize(new Dimension(24, 24));
        check.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Name + description
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        JLabel tName = new JLabel(t[2]);
        tName.setFont(FONT_SUB);
        tName.setForeground(status.equals("COMPLETED") ? TEXT_MUT : TEXT_PRI);
        JLabel tDesc = new JLabel(t[3]);
        tDesc.setFont(FONT_SMALL);
        tDesc.setForeground(TEXT_MUT);
        JLabel tCat = new JLabel(t[4]);
        tCat.setFont(FONT_BADGE);
        tCat.setForeground(ACCENT);
        center.add(tName);
        center.add(tDesc);
        center.add(tCat);

        // Right: due date + status dropdown
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel due = new JLabel("Due: " + t[6]);
        due.setFont(FONT_SMALL);
        due.setForeground(TEXT_MUT);
        due.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton statusBtn = new JButton(formatStatus(status));
        statusBtn.setFont(FONT_BADGE);
        statusBtn.setForeground(statusColor);
        statusBtn.setBackground(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 25));
        statusBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 80), 1, true),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        statusBtn.setFocusPainted(false);
        statusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        statusBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        statusBtn.addActionListener(e -> {
            JPopupMenu pm = new JPopupMenu();
            pm.setBackground(BG_CARD);
            pm.setBorder(new LineBorder(BORDER_COL, 1));
            for (String s : new String[]{"PENDING", "IN_PROGRESS", "COMPLETED"}) {
                JMenuItem mi = new JMenuItem(formatStatus(s));
                mi.setFont(FONT_SMALL);
                mi.setForeground(TEXT_PRI);
                mi.setBackground(BG_CARD);
                mi.addActionListener(ae -> {
                    db.updateTaskStatus(t[0], s);
                    refreshAfterAction();
                });
                pm.add(mi);
            }
            pm.show(statusBtn, 0, statusBtn.getHeight());
        });

        right.add(due);
        right.add(Box.createVerticalStrut(4));
        right.add(statusBtn);

        card.add(check,  BorderLayout.WEST);
        card.add(center, BorderLayout.CENTER);
        card.add(right,  BorderLayout.EAST);

        // Click check to toggle done
        check.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                String next = status.equals("COMPLETED") ? "PENDING" : "COMPLETED";
                db.updateTaskStatus(t[0], next);
                refreshAfterAction();
            }
        });
        return card;
    }

    // ══════════════════════════════ DIALOGS ════════════════════════════════════
    private void showAddCandidateDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                  "Add New Candidate", true);
        dlg.setSize(480, 400);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BG_DEEP);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("New Candidate");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_PRI);

        JPanel form = new JPanel(new GridLayout(5, 2, 12, 10));
        form.setOpaque(false);

        JTextField fName   = new JTextField();
        JTextField fEmail  = new JTextField();
        JTextField fSkills = new JTextField();
        JTextField fPhone  = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(
            new String[]{"PENDING", "IN_PROGRESS", "COMPLETED"}
        );

        styleTextField(fName,  "Full Name");
        styleTextField(fEmail, "name@example.com");
        styleTextField(fSkills,"Skills");
        styleTextField(fPhone, "10-digit phone number");

        form.add(makeLabel("Full Name *")); form.add(fName);
        form.add(makeLabel("Email *"));     form.add(fEmail);
        form.add(makeLabel("Skills *"));    form.add(fSkills);
        form.add(makeLabel("Phone No *"));  form.add(fPhone);
        form.add(makeLabel("Status"));      form.add(statusBox);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton cancel = createSecondaryButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        JButton save = createPrimaryButton("Add Candidate");
        save.setEnabled(false);
        save.setOpaque(true);

        // Disable button until all required fields are non-empty
        Runnable checkFields = () -> {
            boolean filled = !fName.getText().trim().isEmpty()
                          && !fEmail.getText().trim().isEmpty()
                          && !fSkills.getText().trim().isEmpty()
                          && !fPhone.getText().trim().isEmpty();
            save.setEnabled(filled);
            save.setBackground(filled ? new Color(0x4F8EF7) : new Color(0x2A3550));
            save.setForeground(filled ? Color.WHITE : new Color(0x4A5568));
        };
        fName.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkFields.run(); }
        });
        fEmail.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkFields.run(); }
        });
        fSkills.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkFields.run(); }
        });
        fPhone.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { checkFields.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { checkFields.run(); }
        });

        save.addActionListener(e -> {
            String name   = fName.getText().trim();
            String email  = fEmail.getText().trim();
            String skills = fSkills.getText().trim();
            String phone  = fPhone.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            // Email format validation
            if (!email.matches("^[\\w.+\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}$")) {
                JOptionPane.showMessageDialog(dlg,
                    "Please enter a valid email address (e.g. name@domain.com).",
                    "Invalid Email", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Phone format validation — exactly 10 digits
            if (!phone.matches("^[0-9]{10}$")) {
                JOptionPane.showMessageDialog(dlg,
                    "Phone number must be exactly 10 digits.",
                    "Invalid Phone", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String id = "CAND-" + System.currentTimeMillis() % 100000;
            db.addCandidate(id, email, name, phone, skills);
            try {
                db.updateOnboardingStatus(id, status);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(dlg,
                    ex.getMessage(),
                    "Incomplete Tasks", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dlg.dispose();
            loadData();
            selectCandidate(id);
        });

        btnRow.add(cancel);
        btnRow.add(save);

        content.add(title,  BorderLayout.NORTH);
        content.add(form,   BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(content);
        dlg.setVisible(true);
    }

    private void showAddTaskDialog(String candidateId) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                  "Add Task", true);
        dlg.setSize(440, 340);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BG_DEEP);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Add Task");
        title.setFont(FONT_HEAD);
        title.setForeground(TEXT_PRI);

        JPanel form = new JPanel(new GridLayout(4, 2, 12, 10));
        form.setOpaque(false);
        JTextField fName = new JTextField(); styleTextField(fName, "Task Name");
        JTextField fDesc = new JTextField(); styleTextField(fDesc, "Description");
        JTextField fCat  = new JTextField(); styleTextField(fCat,  "Category");
        JTextField fDue  = new JTextField(); styleTextField(fDue,  "Due Date (YYYY-MM-DD)");

        form.add(makeLabel("Task Name"));   form.add(fName);
        form.add(makeLabel("Description")); form.add(fDesc);
        form.add(makeLabel("Category"));    form.add(fCat);
        form.add(makeLabel("Due Date"));    form.add(fDue);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        JButton cancel = createSecondaryButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());
        JButton save = createPrimaryButton("Add Task");
        save.addActionListener(e -> {
            String name = fName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Task name is required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String taskId = "TASK-" + candidateId + "-" + System.currentTimeMillis() % 10000;
            String due = fDue.getText().trim().isEmpty() ? LocalDate.now().plusDays(7).toString()
                                                         : fDue.getText().trim();
            db.addTask(taskId, candidateId, name, fDesc.getText().trim(),
                       fCat.getText().trim(), due);
            dlg.dispose();
            refreshAfterAction();
        });
        btnRow.add(cancel);
        btnRow.add(save);

        content.add(title,  BorderLayout.NORTH);
        content.add(form,   BorderLayout.CENTER);
        content.add(btnRow, BorderLayout.SOUTH);
        dlg.setContentPane(content);
        dlg.setVisible(true);
    }

    private void showStatusMenu(JButton btn, String candidateId) {
        JPopupMenu pm = new JPopupMenu();
        pm.setBackground(BG_CARD);
        pm.setBorder(new LineBorder(BORDER_COL, 1));
        for (String s : new String[]{"PENDING", "IN_PROGRESS", "COMPLETED"}) {
            JMenuItem mi = new JMenuItem(formatStatus(s));
            mi.setFont(FONT_SMALL);
            mi.setForeground(TEXT_PRI);
            mi.setBackground(BG_CARD);
            mi.addActionListener(e -> {
                if ("COMPLETED".equals(s)) {
                    if (!db.areAllTasksCompleted(candidateId)) {
                        JOptionPane.showMessageDialog(this,
                            "Cannot mark as Completed until all tasks for this candidate are completed.",
                            "Incomplete Tasks", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                try {
                    db.updateOnboardingStatus(candidateId, s);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Incomplete Tasks", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                loadData();
                selectCandidate(candidateId);
            });
            pm.add(mi);
        }
        pm.show(btn, 0, btn.getHeight());
    }

    private void refreshAfterAction() {
        loadData();
        if (selectedCandidateId != null) selectCandidate(selectedCandidateId);
    }

    // ══════════════════════════════ HELPERS ════════════════════════════════════
    private JLabel createAvatar(String name) {
        String initials = "";
        for (String part : name.split(" ")) {
            if (!part.isEmpty()) initials += part.charAt(0);
            if (initials.length() == 2) break;
        }
        final String ini = initials.toUpperCase();
        JLabel av = new JLabel(ini, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = name.hashCode();
                Color[] palette = {new Color(0x4F8EF7), new Color(0x6EC6A0), new Color(0xF5A623),
                                   new Color(0xE05C5C), new Color(0xB484E8), new Color(0x4EC9D4)};
                g2.setColor(palette[Math.abs(h) % palette.length]);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini,
                    (getWidth()  - fm.stringWidth(ini)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        av.setPreferredSize(new Dimension(38, 38));
        av.setMinimumSize(new Dimension(38, 38));
        av.setMaximumSize(new Dimension(38, 38));
        return av;
    }

    private JLabel createStatusBadge(String status) {
        Color c = switch (status) {
            case "COMPLETED"   -> C_DONE;
            case "IN_PROGRESS" -> C_INPROG;
            default            -> C_PENDING;
        };
        JLabel badge = new JLabel(formatStatus(status)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_BADGE);
        badge.setForeground(c);
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return badge;
    }

    private JPanel makeDetailField(String label, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUT);
        JLabel val = new JLabel(value == null || value.isEmpty() ? "—" : value);
        val.setFont(FONT_BODY);
        val.setForeground(TEXT_PRI);
        p.add(lbl);
        p.add(val);
        return p;
    }

    private JLabel makeStatPill(String label, int count, Color c) {
        JLabel pill = new JLabel("  " + label + ": " + count + "  ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setFont(FONT_BADGE);
        pill.setForeground(c);
        pill.setOpaque(false);
        pill.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
        return pill;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_SEC);
        return l;
    }

    private void styleTextField(JTextField tf, String placeholder) {
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRI);
        tf.setBackground(BG_CARD);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        tf.putClientProperty("placeholder", placeholder);

        // Placeholder via focus listener
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_PRI);
                }
                ((LineBorder) ((CompoundBorder) tf.getBorder()).getOutsideBorder())
                    .getLineColor();
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ACCENT, 1, true),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)));
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_MUT);
                }
                tf.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COL, 1, true),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)));
            }
        });
        tf.setText(placeholder);
        tf.setForeground(TEXT_MUT);
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), 0,
                                                     new Color(0x3A6FD8));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_SUB);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(BORDER_COL);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_SMALL);
        btn.setForeground(TEXT_SEC);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static String formatStatus(String s) {
        return switch (s) {
            case "IN_PROGRESS" -> "In Progress";
            case "COMPLETED"   -> "Completed";
            default            -> "Pending";
        };
    }

    // ══════════════════════════════ SLIM SCROLLBAR UI ═════════════════════════
    private static class SlimScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor       = new Color(0x3A4F6E);
            trackColor       = new Color(0, 0, 0, 0);
        }
        @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
        @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle t) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(t.x+1, t.y+2, t.width-2, t.height-4, 6, 6);
            g2.dispose();
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle t) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(t.x, t.y, t.width, t.height);
            g2.dispose();
        }
    }

    // ══════════════════════════════ LAUNCHER (standalone) ═════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            JFrame frame = new JFrame("HRMS — Pre-Onboarding");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 780);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null);

            PreOnboardingPage page = new PreOnboardingPage();
            frame.setContentPane(page);
            frame.setVisible(true);
        });
    }
}
