import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String HISTORY_FILE = "dbd_history.txt";
    private static final String KILLER_FILE = "dbd_killers.txt";
    private static final Charset WINDOWS_CHARSET = Charset.forName("MS932");

    // ============================
    // 戦績データの読み込み
    // ============================
    public static List<MatchResult> loadHistory() {
        List<MatchResult> history = new ArrayList<>();
        Path path = Path.of(HISTORY_FILE);

        if (!Files.exists(path)) return history;

        try (BufferedReader br = Files.newBufferedReader(path, WINDOWS_CHARSET)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    history.add(new MatchResult(
                            data[0],
                            Integer.parseInt(data[1]),
                            Integer.parseInt(data[2]),
                            data[3]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("戦績データ読み込みエラー: " + e.getMessage());
        }

        return history;
    }

    // ============================
    // 戦績データの保存
    // ============================
    public static void saveHistory(List<MatchResult> history) {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(HISTORY_FILE), WINDOWS_CHARSET)) {
            for (MatchResult m : history) {
                bw.write(m.getKillerName() + "," +
                         m.getKillCount() + "," +
                         m.getHookCount() + "," +
                         m.getDate());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("戦績データ保存エラー: " + e.getMessage());
        }
    }

    // ============================
    // キラー一覧の読み込み
    // ============================
    public static List<String> loadKillers() {
        List<String> killers = new ArrayList<>();
        Path path = Path.of(KILLER_FILE);

        if (!Files.exists(path)) {
            killers.add("トラッパー");
            saveKillers(killers);
            return killers;
        }

        try (BufferedReader br = Files.newBufferedReader(path, WINDOWS_CHARSET)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) killers.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("キラー一覧読み込みエラー: " + e.getMessage());
        }

        return killers;
    }

    // ============================
    // キラー一覧の保存
    // ============================
    public static void saveKillers(List<String> killers) {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(KILLER_FILE), WINDOWS_CHARSET)) {
            for (String k : killers) {
                bw.write(k);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("キラー一覧保存エラー: " + e.getMessage());
        }
    }
}
