import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class GraphBuilder {

    public static JPanel createDailyLineGraph(List<MatchResult> history) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 日付ごとに集計
        Map<String, List<MatchResult>> dateStats = new HashMap<>();
        for (MatchResult m : history) {
            dateStats.putIfAbsent(m.getDate(), new ArrayList<>());
            dateStats.get(m.getDate()).add(m);
        }

        // 日付順に並べる
        List<String> sortedDates = new ArrayList<>(dateStats.keySet());
        sortedDates.sort(String::compareTo);

        // 直近10日だけ表示
        int start = Math.max(0, sortedDates.size() - 10);

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
                dataset
        );

        // 見た目調整
        var plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4, -4, 8, 8));

        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setDomainGridlinePaint(new Color(220, 220, 220));

        Font jp = new Font("Meiryo", Font.PLAIN, 14);

        chart.getTitle().setFont(new Font("Meiryo", Font.BOLD, 18));
        plot.getDomainAxis().setTickLabelFont(jp);
        plot.getDomainAxis().setLabelFont(jp);
        plot.getRangeAxis().setTickLabelFont(jp);
        plot.getRangeAxis().setLabelFont(jp);

        plot.getRangeAxis().setLabelAngle(Math.PI / 2.0);

        chart.removeLegend();

        return new ChartPanel(chart);
    }
}
