import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsCalculator {

    // ============================
    // 全体戦績
    // ============================
    public static TotalStats calcTotalStats(List<MatchResult> history) {

        int matches = history.size();
        int kills = 0;
        int hooks = 0;
        int wins = 0;

        for (MatchResult m : history) {
            kills += m.getKillCount();
            hooks += m.getHookCount();
            if (m.getKillCount() >= 3) wins++;
        }

        double avgKills = matches == 0 ? 0 : (double) kills / matches;
        double avgHooks = matches == 0 ? 0 : (double) hooks / matches;
        double winRate = matches == 0 ? 0 : ((double) wins / matches) * 100;

        return new TotalStats(matches, avgKills, avgHooks, winRate);
    }

    // ============================
    // キラー別戦績
    // ============================
    public static Map<String, KillerStats> calcKillerStats(List<MatchResult> history) {

        Map<String, List<MatchResult>> grouped = new HashMap<>();

        for (MatchResult m : history) {
            grouped.putIfAbsent(m.getKillerName(), new ArrayList<>());
            grouped.get(m.getKillerName()).add(m);
        }

        Map<String, KillerStats> result = new HashMap<>();

        for (var entry : grouped.entrySet()) {
            String killer = entry.getKey();
            List<MatchResult> matches = entry.getValue();

            int count = matches.size();
            int kills = 0;
            int hooks = 0;
            int wins = 0;

            for (MatchResult m : matches) {
                kills += m.getKillCount();
                hooks += m.getHookCount();
                if (m.getKillCount() >= 3) wins++;
            }

            double avgKills = (double) kills / count;
            double avgHooks = (double) hooks / count;
            double winRate = ((double) wins / count) * 100;

            result.put(killer, new KillerStats(count, avgKills, avgHooks, winRate));
        }

        return result;
    }

    // ============================
    // 日付別戦績
    // ============================
    public static Map<String, DateStats> calcDateStats(List<MatchResult> history) {

        Map<String, List<MatchResult>> grouped = new HashMap<>();

        for (MatchResult m : history) {
            grouped.putIfAbsent(m.getDate(), new ArrayList<>());
            grouped.get(m.getDate()).add(m);
        }

        Map<String, DateStats> result = new HashMap<>();

        for (var entry : grouped.entrySet()) {
            String date = entry.getKey();
            List<MatchResult> matches = entry.getValue();

            int count = matches.size();
            int kills = 0;
            int hooks = 0;
            int wins = 0;

            for (MatchResult m : matches) {
                kills += m.getKillCount();
                hooks += m.getHookCount();
                if (m.getKillCount() >= 3) wins++;
            }

            double avgKills = (double) kills / count;
            double avgHooks = (double) hooks / count;
            double winRate = ((double) wins / count) * 100;

            result.put(date, new DateStats(count, avgKills, avgHooks, winRate));
        }

        return result;
    }

    // ============================
    // 日付フィルタ戦績
    // ============================
    public static DateStats calcFilteredStats(List<MatchResult> history, String filterDate) {

        int matches = 0;
        int kills = 0;
        int hooks = 0;
        int wins = 0;

        for (MatchResult m : history) {
            if (!m.getDate().equals(filterDate)) continue;

            matches++;
            kills += m.getKillCount();
            hooks += m.getHookCount();
            if (m.getKillCount() >= 3) wins++;   // ★ 3K以上を勝ちとみなす
        }

        if (matches == 0) {
            return new DateStats(0, 0, 0, 0);
        }

        double avgKills = (double) kills / matches;
        double avgHooks = (double) hooks / matches;
        double winRate = ((double) wins / matches) * 100;  // ★ 勝率[%]

        return new DateStats(matches, avgKills, avgHooks, winRate);
    }

}
