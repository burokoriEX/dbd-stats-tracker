public class DateStats {
    public final int matches;
    public final double avgKills;
    public final double avgHooks;
    public final double winRate;

    public DateStats(int matches, double avgKills, double avgHooks, double winRate) {
        this.matches = matches;
        this.avgKills = avgKills;
        this.avgHooks = avgHooks;
        this.winRate = winRate;
    }
}
