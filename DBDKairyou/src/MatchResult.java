public class MatchResult {
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