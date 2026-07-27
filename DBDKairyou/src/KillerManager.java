import java.util.List;

public class KillerManager {

    private List<String> killers;

    public KillerManager() {
        this.killers = DataManager.loadKillers();
    }

    public List<String> getKillers() {
        return killers;
    }

    public void addKiller(String name) {
        if (name == null || name.isBlank()) return;
        killers.add(name);
        DataManager.saveKillers(killers);
    }

    public boolean deleteKiller(String name) {
        boolean removed = killers.remove(name);
        if (removed) {
            DataManager.saveKillers(killers);
        }
        return removed;
    }
}
