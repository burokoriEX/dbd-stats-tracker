import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class DBDdataKAI3 {

	private static final String FILE_NAME = "dbd_history.txt";
	private static final Charset WINDOWS_CHARSET = Charset.forName("MS932");

	private static List<MatchResult> history = new ArrayList<>();
	private static JComboBox<String> cmbKiller;
	private static JTextArea txtAreaStats;

	private static int currentKill = 0;
	private static int currentHook = 0;
	private static JLabel lblKillVal;
	private static JLabel lblHookVal;
	private static JComboBox<String> dateFilterBox;
	Map<String, Double> averageMap;
	private static List<String> killerList = new ArrayList<>();
	private static final String KILLER_FILE = "dbd_killers.txt";

	
	public static class MatchResult {
		private final String killerName;
		private final int killCount;
		private final int hookCount;
		private final String date;

		public MatchResult(String killerName, int killCount, int hookCount, String date) {
			this.killerName = killerName;
			this.killCount = killCount;
			this.hookCount = hookCount;
			this.date = date;
		}

		public String getKillerName() {
			return killerName;
		}

		public int getKillCount() {
			return killCount;
		}

		public int getHookCount() {
			return hookCount;
		}

		public String getDate() {
			return date;
		}
	}
	
	private static void loadKillers() {
	    Path path = Path.of(KILLER_FILE);

	    if (!Files.exists(path)) {
	        killerList.addAll(List.of(
	            "ダークロード", "リッチ", "金木", "ウェスカー", "ジェイソン",
	            "トリックスター", "ナース", "スピリット", "アニマトロニクス",
	            "セノバイト", "シェイプ", "レイス", "カニバル", "ネメシス",
	            "ナイトメア", "リージョン"
	        ));
	        saveKillers();
	        return;
	    }

	    try (BufferedReader br = Files.newBufferedReader(path, WINDOWS_CHARSET)) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            if (!line.isBlank()) killerList.add(line.trim());
	        }
	    } catch (IOException e) {
	        System.out.println("キラー一覧の読み込みエラー: " + e.getMessage());
	    }
	}

	private static void saveKillers() {
	    try (BufferedWriter bw = Files.newBufferedWriter(Path.of(KILLER_FILE), WINDOWS_CHARSET)) {
	        for (String k : killerList) {
	            bw.write(k);
	            bw.newLine();
	        }
	    } catch (IOException e) {
	        System.out.println("キラー一覧の保存エラー: " + e.getMessage());
	    }
	}



	   


	public static void main(String[] args) {
		loadData(history);
		SwingUtilities.invokeLater(DBDdataKAI3::createAndShowGUI);
	}

	private static void createAndShowGUI() {
		
		loadKillers();  // ★ 追加
		cmbKiller = new JComboBox<>(killerList.toArray(new String[0]));  // ★ killers を使わない

		cmbKiller.setEditable(false);

		JFrame frame = new JFrame("DbD戦績管理システム GUI版");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout(10, 10));

		// 入力パネル
		JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
		inputPanel.setBorder(BorderFactory.createTitledBorder("試合データの入力"));

		

		inputPanel.add(new JLabel("使用したキラー名:"));
		inputPanel.add(cmbKiller);

		// キル数
		inputPanel.add(new JLabel(" キルした人数 (0-4):"));
		JPanel killPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		JButton btnKillMinus = new JButton("ー");
		JButton btnKillPlus = new JButton("＋1");
		lblKillVal = new JLabel("0");
		killPanel.add(btnKillMinus);
		killPanel.add(lblKillVal);
		killPanel.add(btnKillPlus);
		inputPanel.add(killPanel);

		// フック数
		inputPanel.add(new JLabel(" フックに吊った回数 (0-12):"));
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

		// キル数イベント
		btnKillMinus.addActionListener(e -> {
			if (currentKill > 0)
				currentKill--;
			lblKillVal.setText(String.valueOf(currentKill));
		});
		btnKillPlus.addActionListener(e -> {
			if (currentKill < 4)
				currentKill++;
			lblKillVal.setText(String.valueOf(currentKill));
		});

		// フック数イベント
		btnHookMinus.addActionListener(e -> {
			if (currentHook > 0)
				currentHook--;
			lblHookVal.setText(String.valueOf(currentHook));
		});
		btnHookPlus.addActionListener(e -> {
			if (currentHook < 12)
				currentHook++;
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

		// ボタンパネル
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		JButton btnRegister = new JButton("データを登録");
		JButton btnReset = new JButton("戦績リセット");
		buttonPanel.add(btnRegister);
		buttonPanel.add(btnReset);
		JButton btnAddKiller = new JButton("キラー追加");
		btnAddKiller.addActionListener(e -> {
		    String newKiller = JOptionPane.showInputDialog("追加するキラー名を入力してください");
		    if (newKiller != null && !newKiller.isBlank()) {
		        killerList.add(newKiller);
		        cmbKiller.addItem(newKiller);
		        saveKillers();
		        JOptionPane.showMessageDialog(frame, "新しいキラーを追加しました！");
		    }
		});
		buttonPanel.add(btnAddKiller);
		JButton btnDeleteKiller = new JButton("キラー削除");
		btnDeleteKiller.addActionListener(e -> {
		    String selected = (String) cmbKiller.getSelectedItem();
		    if (selected == null) {
		        JOptionPane.showMessageDialog(frame, "削除するキラーが選択されていません。");
		        return;
		    }

		    int confirm = JOptionPane.showConfirmDialog(
		        frame,
		        selected + " を削除しますか？",
		        "キラー削除確認",
		        JOptionPane.YES_NO_OPTION
		    );

		    if (confirm == JOptionPane.YES_OPTION) {
		        killerList.remove(selected);
		        cmbKiller.removeItem(selected);
		        saveKillers();
		        JOptionPane.showMessageDialog(frame, "キラーを削除しました！");
		    }
		});
		buttonPanel.add(btnDeleteKiller);



		// 日付フィルタ
		JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		dateFilterBox = new JComboBox<>();

		JButton btnShowAll = new JButton("全体表示");

		Set<String> dates = new HashSet<>();
		for (MatchResult m : history)
			dates.add(m.getDate());
		for (String d : dates)
			dateFilterBox.addItem(d);

		filterPanel.add(new JLabel("日付フィルタ:"));
		filterPanel.add(dateFilterBox);
		filterPanel.add(btnShowAll);

		// 上部パネル
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(inputPanel, BorderLayout.CENTER);
		topPanel.add(buttonPanel, BorderLayout.SOUTH);
		topPanel.add(filterPanel, BorderLayout.NORTH);



		// 結果表示
		txtAreaStats = new JTextArea();
		txtAreaStats.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtAreaStats);
		scrollPane.setBorder(BorderFactory.createTitledBorder("現在の戦績データ一覧"));

		frame.add(topPanel, BorderLayout.NORTH);

		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("戦績一覧", scrollPane);
		//		tabs.addTab("グラフ", createKillGraph());
		tabs.addTab("日付折れ線グラフ", createDailyLineGraph());

		frame.add(tabs, BorderLayout.CENTER);

		// イベント
		btnRegister.addActionListener(e -> {
			String killer = cmbKiller.getSelectedItem().toString();
			if (killer.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "キラー名を入力してください。", "入力エラー", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String date = java.time.LocalDate.now().toString();
			MatchResult res = new MatchResult(killer, currentKill, currentHook, date);
			history.add(res);
			saveData(history);

			updateStatsDisplay(date);
			
			refreshDateFilter();                // ★ 日付フィルタ更新
		    tabs.setComponentAt(1, createDailyLineGraph()); // ★ グラフ更新
		    
			tabs.setComponentAt(1, createDailyLineGraph());

			cmbKiller.setSelectedIndex(0);
			currentKill = 0;
			currentHook = 0;
			lblKillVal.setText("0");
			lblHookVal.setText("0");

			JOptionPane.showMessageDialog(frame, "データを保存しました！");
			
			updateStatsDisplayAll();  // ★ 全体表示に戻す

		});

		btnReset.addActionListener(e -> {
			int select = JOptionPane.showConfirmDialog(frame, "本当にこれまでの戦績をすべて消去しますか？", "戦績リセット確認",
					JOptionPane.YES_NO_OPTION);
			if (select == JOptionPane.YES_OPTION) {
				history.clear();
				try {
					Files.deleteIfExists(Path.of(FILE_NAME));
					updateStatsDisplayAll();
					JOptionPane.showMessageDialog(frame, "全ての戦績データをリセットしました！");
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, "ファイルの削除に失敗しました: " + ex.getMessage(), "エラー",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		dateFilterBox.addActionListener(e -> {
			String selectedDate = (String) dateFilterBox.getSelectedItem();
			updateStatsDisplay(selectedDate);
		});

		btnShowAll.addActionListener(e -> updateStatsDisplayAll());

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		updateStatsDisplayAll(); // 初期表示
	}

	// ★ 全体表示
	private static void updateStatsDisplayAll() {

		if (history.isEmpty()) {
			txtAreaStats.setText("まだ試合データがありません。データを入力してください。");
			return;
		}

		StringBuilder sb = new StringBuilder();
		int totalMatches = history.size();
		int totalKills = 0;
		int totalHooks = 0;
		int totalWins = 0;   // ★ 追加

		Map<String, List<MatchResult>> killerStats = new HashMap<>();
		Map<String, List<MatchResult>> dateStats = new HashMap<>();

		for (MatchResult m : history) {
			totalKills += m.getKillCount();
			totalHooks += m.getHookCount();
			 if (m.getKillCount() >= 3) totalWins++;  // ★ 追加
		

			killerStats.putIfAbsent(m.getKillerName(), new ArrayList<>());
			killerStats.get(m.getKillerName()).add(m);

			dateStats.putIfAbsent(m.getDate(), new ArrayList<>());
			dateStats.get(m.getDate()).add(m);
			
		}

		double avgKills = (double) totalKills / totalMatches;
		double avgHooks = (double) totalHooks / totalMatches;
		
		double totalWinRate = ((double) totalWins / totalMatches) * 100;  // ★ 追加

		sb.append("====== 【全体・通算戦績】 ======\n");
		sb.append("総試合数　: ").append(totalMatches).append(" 試合\n");
		sb.append("平均キル数: ").append(String.format("%.2f", avgKills)).append(" 人\n");
		sb.append("平均フック: ").append(String.format("%.2f", avgHooks)).append(" 回\n\n");
		sb.append("勝率(3K以上): ").append(String.format("%.1f", totalWinRate)).append("%\n\n");  // ★ 追加

		sb.append("====== 【キラー別戦績】 ======\n");
		for (Map.Entry<String, List<MatchResult>> entry : killerStats.entrySet()) {
			String killerName = entry.getKey();
			List<MatchResult> matches = entry.getValue();

			int kMatches = matches.size();
			int kKills = 0;
			int kHooks = 0;
			int winGames = 0;

			for (MatchResult m : matches) {
				kKills += m.getKillCount();
				kHooks += m.getHookCount();
				if (m.getKillCount() >= 3)
					winGames++;
			}

			double kAvgKills = (double) kKills / kMatches;
			double kAvgHooks = (double) kHooks / kMatches;
			double winRate = ((double) winGames / kMatches) * 100;

			sb.append("[").append(killerName).append("] ").append(kMatches).append("試合\n");
			sb.append("  ➔ 平均キル: ").append(String.format("%.2f", kAvgKills)).append("人")
					.append(" | 平均フック: ").append(String.format("%.2f", kAvgHooks)).append("回")
					.append(" | 勝率(3K以上): ").append(String.format("%.1f", winRate)).append("%\n");
		}

		sb.append("\n====== 【日付ごとの戦績】 ======\n");
		for (Map.Entry<String, List<MatchResult>> entry : dateStats.entrySet()) {
			String date = entry.getKey();
			List<MatchResult> matches = entry.getValue();

			int dMatches = matches.size();
			int dKills = 0;
			int dHooks = 0;
			int dWins = 0;

			for (MatchResult m : matches) {
				dKills += m.getKillCount();
				dHooks += m.getHookCount();
				if (m.getKillCount() >= 3)
					dWins++;
			}

			double kAvgKills = (double) dKills / dMatches;
			double kAvgHooks = (double) dHooks / dMatches;
			double winRate = ((double) dWins / dMatches) * 100;

			sb.append("[").append(date).append("] ").append(dMatches).append("試合\n");
			sb.append("  ➔ 平均キル: ").append(String.format("%.2f", kAvgKills)).append("人")
					.append(" | 平均フック: ").append(String.format("%.2f", kAvgHooks)).append("回")
					.append(" | 勝率(3K以上): ").append(String.format("%.1f", winRate)).append("%\n");
		}

		txtAreaStats.setText(sb.toString());
	}

	// ★ 日付フィルタ表示
	private static void updateStatsDisplay(String filterDate) {

		if (filterDate == null) {
			updateStatsDisplayAll();
			return;
		}

		StringBuilder sb = new StringBuilder();
		int totalMatches = 0;
		int totalKills = 0;
		int totalHooks = 0;

		Map<String, List<MatchResult>> killerStats = new HashMap<>();

		for (MatchResult m : history) {
			if (!m.getDate().equals(filterDate))
				continue;

			totalMatches++;
			totalKills += m.getKillCount();
			totalHooks += m.getHookCount();

			killerStats.putIfAbsent(m.getKillerName(), new ArrayList<>());
			killerStats.get(m.getKillerName()).add(m);
		}

		if (totalMatches == 0) {
			txtAreaStats.setText(filterDate + " の試合データはありません。");
			return;
		}

		double avgKills = (double) totalKills / totalMatches;
		double avgHooks = (double) totalHooks / totalMatches;

		sb.append("====== 【").append(filterDate).append(" の戦績】 ======\n");
		sb.append("該当日試合数　: ").append(totalMatches).append(" 試合\n");
		sb.append("平均キル数: ").append(String.format("%.2f", avgKills)).append(" 人\n");
		sb.append("平均フック: ").append(String.format("%.2f", avgHooks)).append(" 回\n\n");

		sb.append("====== 【キラー別戦績】 ======\n");
		for (Map.Entry<String, List<MatchResult>> entry : killerStats.entrySet()) {
			String killerName = entry.getKey();
			List<MatchResult> matches = entry.getValue();

			int kMatches = matches.size();
			int kKills = 0;
			int kHooks = 0;
			int winGames = 0;

			for (MatchResult m : matches) {
				kKills += m.getKillCount();
				kHooks += m.getHookCount();
				if (m.getKillCount() >= 3)
					winGames++;
			}

			double kAvgKills = (double) kKills / kMatches;
			double kAvgHooks = (double) kHooks / kMatches;
			double winRate = ((double) winGames / kMatches) * 100;

			sb.append("[").append(killerName).append("] ").append(kMatches).append("試合\n");
			sb.append("  ➔ 平均キル: ").append(String.format("%.2f", kAvgKills)).append("人")
					.append(" | 平均フック: ").append(String.format("%.2f", kAvgHooks)).append("回")
					.append(" | 勝率(3K以上): ").append(String.format("%.1f", winRate)).append("%\n");
		}

		txtAreaStats.setText(sb.toString());
	}

	private static void saveData(List<MatchResult> history) {
		try (BufferedWriter bw = Files.newBufferedWriter(Path.of(FILE_NAME), WINDOWS_CHARSET)) {
			for (MatchResult m : history) {
				bw.write(m.getKillerName() + "," + m.getKillCount() + "," + m.getHookCount() + "," + m.getDate());
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("データの保存中にエラーが発生しました: " + e.getMessage());
		}
	}

	private static void loadData(List<MatchResult> history) {
		Path path = Path.of(FILE_NAME);
		if (!Files.exists(path))
			return;

		try (BufferedReader br = Files.newBufferedReader(path, WINDOWS_CHARSET)) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				if (data.length == 4) {
					history.add(new MatchResult(
							data[0],
							Integer.parseInt(data[1]),
							Integer.parseInt(data[2]),
							data[3]));
				}
			}
		} catch (IOException | NumberFormatException e) {
			System.out.println("データの読み込みエラー");
		}
	}

	//	private static JPanel createKillGraph() {
	//
	//		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	//
	//		// キラー別平均キルを dataset に入れる
	//		Map<String, List<MatchResult>> killerStats = new HashMap<>();
	//		for (MatchResult m : history) {
	//			killerStats.putIfAbsent(m.getKillerName(), new ArrayList<>());
	//			killerStats.get(m.getKillerName()).add(m);
	//		}
	//
	//		for (Map.Entry<String, List<MatchResult>> entry : killerStats.entrySet()) {
	//			String killer = entry.getKey();
	//			List<MatchResult> matches = entry.getValue();
	//
	//			double totalKills = 0;
	//			for (MatchResult m : matches)
	//				totalKills += m.getKillCount();
	//
	//			double avgKills = totalKills / matches.size();
	//
	//			dataset.addValue(avgKills, "平均キル数", killer);
	//		}
	//
	//		JFreeChart chart = ChartFactory.createBarChart(
	//				"キラー別 平均キル数グラフ",
	//				"キラー",
	//				"平均キル数",
	//				dataset);
	//
	//		return new ChartPanel(chart);
	//	}
	private static JPanel createDailyLineGraph() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// 日付ごとに集計
		Map<String, List<MatchResult>> dateStats = new HashMap<>();
		for (MatchResult m : history) {
			dateStats.putIfAbsent(m.getDate(), new ArrayList<>());
			dateStats.get(m.getDate()).add(m);
		}

		// 日付順に並べる
		// 日付順に並べる
		List<String> sortedDates = new ArrayList<>(dateStats.keySet());
		sortedDates.sort(String::compareTo);

		// 直近10日だけ表示
		int start = Math.max(0, sortedDates.size() - 10);

		// データセットに追加
		for (int i = start; i < sortedDates.size(); i++) {

		    String date = sortedDates.get(i);
		    List<MatchResult> matches = dateStats.get(date);

		    double totalKills = 0;
		    for (MatchResult m : matches) {
		        totalKills += m.getKillCount();
		    }

		    double avgKills = totalKills / matches.size();

		    dataset.addValue(avgKills, "平均キル数", date);
		}

		// グラフ生成
		JFreeChart chart = ChartFactory.createLineChart(
				"日付ごとの平均キル数（折れ線グラフ）",
				"日付",
				"平均キル数",
				dataset);

		// ★ 見やすさ改善セット
		var plot = chart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

		// 線を太くする
		renderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f));

		// データ点を丸く表示
		renderer.setSeriesShapesVisible(0, true);
		renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));

		// 背景を白にする
		plot.setBackgroundPaint(java.awt.Color.WHITE);

		// グリッド線を薄くする
		plot.setRangeGridlinePaint(new java.awt.Color(200, 200, 200));
		plot.setDomainGridlinePaint(new java.awt.Color(220, 220, 220));

		// 日本語フォント（メイリオ）
		java.awt.Font jp = new java.awt.Font("Meiryo", java.awt.Font.PLAIN, 14);

		// タイトル
		chart.getTitle().setFont(new java.awt.Font("Meiryo", java.awt.Font.BOLD, 18));

		// X軸
		plot.getDomainAxis().setTickLabelFont(jp);
		plot.getDomainAxis().setLabelFont(jp);

		// Y軸
		plot.getRangeAxis().setTickLabelFont(jp);
		plot.getRangeAxis().setLabelFont(jp);

		// ★ Y軸ラベルを縦向きに
		plot.getRangeAxis().setLabelAngle(Math.PI / 2.0);

		// 凡例を消す（折れ線は不要）
		chart.removeLegend();

		return new ChartPanel(chart);
	}

	private static void refreshDateFilter() {
		dateFilterBox.removeAllItems();

		Set<String> dates = new HashSet<>();
		for (MatchResult m : history)
			dates.add(m.getDate());

		List<String> sorted = new ArrayList<>(dates);
		sorted.sort(String::compareTo);

		for (String d : sorted)
			dateFilterBox.addItem(d);
	}

}
