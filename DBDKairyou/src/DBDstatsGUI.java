import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class DBDstatsGUI {

    private List<MatchResult> history;
    private KillerManager killerManager;

    private JComboBox<String> cmbKiller;
    private JComboBox<String> dateFilterBox;
    private JTextArea txtAreaStats;

    private int currentKill = 0;
    private int currentHook = 0;
    private JLabel lblKillVal;
    private JLabel lblHookVal;

    public void start() {
        history = DataManager.loadHistory();
        killerManager = new KillerManager();

        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {

        JFrame frame = new JFrame("DbD戦績管理システム GUI版");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));

        // ============================
        // キラー選択
        // ============================
        cmbKiller = new JComboBox<>(killerManager.getKillers().toArray(new String[0]));

        // ============================
        // 入力パネル
        // ============================
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("試合データの入力"));

        inputPanel.add(new JLabel("使用したキラー名:"));
        inputPanel.add(cmbKiller);

        // キル数
        inputPanel.add(new JLabel("キルした人数 (0-4):"));
        JPanel killPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnKillMinus = new JButton("ー");
        JButton btnKillPlus = new JButton("＋1");
        lblKillVal = new JLabel("0");
        killPanel.add(btnKillMinus);
        killPanel.add(lblKillVal);
        killPanel.add(btnKillPlus);
        inputPanel.add(killPanel);

        btnKillMinus.addActionListener(e -> {
            if (currentKill > 0) currentKill--;
            lblKillVal.setText(String.valueOf(currentKill));
        });
        btnKillPlus.addActionListener(e -> {
            if (currentKill < 4) currentKill++;
            lblKillVal.setText(String.valueOf(currentKill));
        });

        // フック数
        inputPanel.add(new JLabel("フックに吊った回数 (0-12):"));
        JPanel hookPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnHookMinus = new JButton("ー");
        JButton btnHookPlus = new JButton("＋1");
        JButton btnHookPlus2 = new JButton("＋2");
        JButton btnHookPlus3 = new JButton("＋3");
        lblHookVal = new JLabel("0");
        hookPanel.add(btnHookMinus);
        hookPanel.add(lblHookVal);
        hookPanel.add(btnHookPlus);
        hookPanel.add(btnHookPlus2);
        hookPanel.add(btnHookPlus3);
        inputPanel.add(hookPanel);

        btnHookMinus.addActionListener(e -> {
            if (currentHook > 0) currentHook--;
            lblHookVal.setText(String.valueOf(currentHook));
        });
        btnHookPlus.addActionListener(e -> {
            if (currentHook < 12) currentHook++;
            lblHookVal.setText(String.valueOf(currentHook));
        });
        btnHookPlus2.addActionListener(e -> {
            currentHook = Math.min(currentHook + 2, 12);
            lblHookVal.setText(String.valueOf(currentHook));
        });
        btnHookPlus3.addActionListener(e -> {
            currentHook = Math.min(currentHook + 3, 12);
            lblHookVal.setText(String.valueOf(currentHook));
        });

        // ============================
        // ボタンパネル
        // ============================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnRegister = new JButton("データを登録");
        JButton btnReset = new JButton("戦績リセット");
        JButton btnAddKiller = new JButton("キラー追加");
        JButton btnDeleteKiller = new JButton("キラー削除");

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnAddKiller);
        buttonPanel.add(btnDeleteKiller);

        // キラー追加
        btnAddKiller.addActionListener(e -> {
            String newKiller = JOptionPane.showInputDialog("追加するキラー名を入力してください");
            if (newKiller != null && !newKiller.isBlank()) {
                killerManager.addKiller(newKiller);
                cmbKiller.addItem(newKiller);
                JOptionPane.showMessageDialog(frame, "新しいキラーを追加しました！");
            }
        });

        // キラー削除
        btnDeleteKiller.addActionListener(e -> {
            String selected = (String) cmbKiller.getSelectedItem();
            if (selected == null) return;

            int confirm = JOptionPane.showConfirmDialog(frame,
                    selected + " を削除しますか？",
                    "キラー削除確認",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (killerManager.deleteKiller(selected)) {
                    cmbKiller.removeItem(selected);
                    JOptionPane.showMessageDialog(frame, "キラーを削除しました！");
                }
            }
        });

        // ============================
        // 日付フィルタ
        // ============================
        dateFilterBox = new JComboBox<>();

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("日付フィルタ:"));
        filterPanel.add(dateFilterBox);

        dateFilterBox.addActionListener(e -> {
            String selected = (String) dateFilterBox.getSelectedItem();
            updateFilterStats(selected);
        });

        // ============================
        // 上部パネル
        // ============================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        // ============================
        // 結果表示
        // ============================
        txtAreaStats = new JTextArea();
        txtAreaStats.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaStats);

     // ★ ここで初めて呼ぶ
        refreshDateFilter();
        // ============================
        // タブ
        // ============================
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("フィルタ戦績", scrollPane);
        tabs.addTab("全体戦績", createTotalStatsPanel());
        tabs.addTab("キラー別戦績", createKillerStatsPanel());
        tabs.addTab("日付別戦績", createDateStatsPanel());
        tabs.addTab("日付折れ線グラフ", GraphBuilder.createDailyLineGraph(history));

        frame.add(tabs, BorderLayout.CENTER);

        // ============================
        // 登録ボタン
        // ============================
        btnRegister.addActionListener(e -> {
            String killer = cmbKiller.getSelectedItem().toString();
            String date = LocalDate.now().toString();

            MatchResult res = new MatchResult(killer, currentKill, currentHook, date);
            history.add(res);
            DataManager.saveHistory(history);

            refreshDateFilter();
            updateFilterStats(date);

            tabs.setComponentAt(4, GraphBuilder.createDailyLineGraph(history));

            currentKill = 0;
            currentHook = 0;
            lblKillVal.setText("0");
            lblHookVal.setText("0");

            JOptionPane.showMessageDialog(frame, "データを保存しました！");
        });

        // ============================
        // リセットボタン
        // ============================
        btnReset.addActionListener(e -> {
            int select = JOptionPane.showConfirmDialog(frame,
                    "本当にこれまでの戦績をすべて消去しますか？",
                    "戦績リセット確認",
                    JOptionPane.YES_NO_OPTION);

            if (select == JOptionPane.YES_OPTION) {
                history.clear();
                DataManager.saveHistory(history);
                txtAreaStats.setText("まだ試合データがありません。");
                refreshDateFilter();
                JOptionPane.showMessageDialog(frame, "全ての戦績データをリセットしました！");
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ============================
    // 戦績表示（フィルタ）
    // ============================
    private void updateFilterStats(String date) {
        DateStats ds = StatsCalculator.calcFilteredStats(history, date);

        if (ds.matches == 0) {
            txtAreaStats.setText(date + " の試合データはありません。");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====== 【").append(date).append(" の戦績】 ======\n");
        sb.append("試合数              : ").append(ds.matches).append("試合\n");
        sb.append("平均キル数      : ").append(String.format("%.2f", ds.avgKills)).append("人\n");
        sb.append("平均フック数  : ").append(String.format("%.2f", ds.avgHooks)).append("回\n");
        sb.append("勝率(3K以上)  : ").append(String.format("%.1f", ds.winRate)).append("%\n");

        txtAreaStats.setText(sb.toString());
    }

    // ============================
    // 全体戦績パネル
    // ============================
    private JScrollPane createTotalStatsPanel() {
        JTextArea area = new JTextArea();
        area.setEditable(false);

        TotalStats ts = StatsCalculator.calcTotalStats(history);

        StringBuilder sb = new StringBuilder();
        sb.append("====== 【全体・通算戦績】 ======\n");
        sb.append("総試合数          : ").append(ts.matches).append("試合\n");
        sb.append("平均キル数      : ").append(String.format("%.2f", ts.avgKills)).append("人\n");
        sb.append("平均フック数  : ").append(String.format("%.2f", ts.avgHooks)).append("回\n");
        sb.append("勝率(3K以上)  : ").append(String.format("%.1f", ts.winRate)).append("%\n");

        area.setText(sb.toString());
        return new JScrollPane(area);
    }

    // ============================
    // キラー別戦績パネル
    // ============================
    private JScrollPane createKillerStatsPanel() {
        JTextArea area = new JTextArea();
        area.setEditable(false);

        Map<String, KillerStats> ks = StatsCalculator.calcKillerStats(history);

        StringBuilder sb = new StringBuilder();
        sb.append("====== 【キラー別戦績】 ======\n");

        for (var entry : ks.entrySet()) {
            String killer = entry.getKey();
            KillerStats s = entry.getValue();

            sb.append("[").append(killer).append("] ").append(s.matches).append("試合\n");
            sb.append("  ➔ 平均キル: ").append(String.format("%.2f", s.avgKills)).append("人")
              .append(" | 平均フック: ").append(String.format("%.2f", s.avgHooks)).append("回")
              .append(" | 勝率(3K以上): ").append(String.format("%.1f", s.winRate)).append("%\n");
        }

        area.setText(sb.toString());
        return new JScrollPane(area);
    }

    // ============================
    // 日付別戦績パネル
    // ============================
    private JScrollPane createDateStatsPanel() {
        JTextArea area = new JTextArea();
        area.setEditable(false);

        Map<String, DateStats> ds = StatsCalculator.calcDateStats(history);

        StringBuilder sb = new StringBuilder();
        sb.append("====== 【日付ごとの戦績】 ======\n");

        for (var entry : ds.entrySet()) {
            String date = entry.getKey();
            DateStats s = entry.getValue();

            sb.append("[").append(date).append("] ").append(s.matches).append("試合\n");
            sb.append("  ➔ 平均キル: ").append(String.format("%.2f", s.avgKills)).append("人")
              .append(" | 平均フック: ").append(String.format("%.2f", s.avgHooks)).append("回")
              .append(" | 勝率(3K以上): ").append(String.format("%.1f", s.winRate)).append("%\n");
        }

        area.setText(sb.toString());
        return new JScrollPane(area);
    }

    // ============================
    // 日付フィルタ更新
    // ============================
    private void refreshDateFilter() {
        dateFilterBox.removeAllItems();

        // ★ 重複を消すために Set を使う
        Set<String> dateSet = new HashSet<>();
        for (MatchResult m : history) {
            dateSet.add(m.getDate());
        }

        // ★ ソートするために List に戻す
        List<String> dates = new ArrayList<>(dateSet);
        dates.sort(String::compareTo);   // 昇順（古い → 新しい）

        for (String d : dates) {
            dateFilterBox.addItem(d);
        }

        // ★ 最新の日付を選択する
        if (!dates.isEmpty()) {
            String newest = dates.get(dates.size() - 1);  // ← 最新日付
            dateFilterBox.setSelectedItem(newest);
            updateFilterStats(newest);                    // ← 最新日付の戦績を表示
        }
    }


}
