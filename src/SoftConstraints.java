import java.util.ArrayList;
import java.util.Arrays;

public class SoftConstraints {
	
	int totalPenalties = 0;
	String[] finished = new String[8];
	
	public int getTotalPenalties() { return totalPenalties; }
	
	public String[] setPenalties(Scheduler s, HardConstraints hc) {
		int[] matches = branchAndBound(s,hc,totalPenalties);
		for (int i=0; i<matches.length; i++) {
			ArrayList<String> tasks = s.getTasks();
			finished[i] = tasks.get(matches[i]);
		}
		return finished;
	}
	
	private int[] branchAndBound(Scheduler s, HardConstraints hc, int total) {
		ArrayList<ArrayList<Integer>> grid = s.getMachinePenalties();
		ArrayList<String> machines = s.getMachines();
		ArrayList<String> tasks = s.getTasks();
		
		int[] matches = new int[]{-1,-1,-1,-1,-1,-1,-1,-1};
		
		// Find best task for each machine by iterating over possible total penalty scores
		// when machine[i] is a forced selection
		for (int mach=0; mach<grid.size(); mach++) {
			ArrayList<Integer> row = grid.get(mach);
			// Start with first value in row
			int forcedTask = row.get(0);
			total = iterateRound(s,mach,0,forcedTask,grid,matches);
			matches[mach] = 0;
			// Iterate over row
			for (int task=1; task<row.size(); task++) {
				// Check only if spot not taken by previous row
				// And pairing does not violate too-near invalid pairs
				if (taskAvailable(matches,task) && hc.isValidPair(s.getForbiddenPairs(), machines.get(mach), tasks.get(task))) {
					int taskPenalty = row.get(task);
					int roundTotal = iterateRound(s,mach,task,taskPenalty,grid,matches);
					if (roundTotal < total) {
						total = roundTotal;
						matches[mach] = task;
						// Add new forbidden pairs for new match
						checkTooNearInvalid(s,hc,machines,tasks,mach,task);
					}
				}
			}
		}
		
		return matches;
	}
	
	// Check if task is already been previously matched
	private boolean taskAvailable(int[] matches, int task) {
		boolean available = true;
		for (int t : matches) {
			if (task == t) { available = false; }
		}
		return available;
	}
	
	private int iterateRound(Scheduler s, 
			int mach, int forcedTask, int taskPenalty,
			ArrayList<ArrayList<Integer>> grid, int[] matches) {
		ArrayList<ArrayList<String>> tooNearList = s.getTooNearPenalties();
		ArrayList<String> tasks = s.getTasks();
		// Starting values
		int[] roundMatches = matches;
		roundMatches[mach] = forcedTask;
		int totalScore = taskPenalty;
		// Check all rows below current i
		for (int j=mach+1; j<grid.size(); j++) {
			ArrayList<Integer> row = grid.get(j);
			int minValue = row.get(0);
			// Find lowest value in row
			for (int t=1; t<row.size(); t++) {
				// Check only if free
				if (taskAvailable(roundMatches,t)) {
					int newValue = row.get(t) + tooNearPenalty(tasks,j,t,roundMatches,tooNearList);
					if (newValue < minValue) {
						minValue = newValue;
						roundMatches[j] = t;
					}
				}
			}
			// Add lowest row value to total
			totalScore += minValue;
		}
		return totalScore;
	}
	
	private int tooNearPenalty(ArrayList<String> tasks, int machine, int taskIndex,
			int[] matches, ArrayList<ArrayList<String>> tooNearList) {
		int penalty = 0;
		String taskLetter = tasks.get(taskIndex);
		// Loop through all too-near triples
		for (ArrayList<String> penalties : tooNearList) {
			// Split values of too-near penalty
			String leftTask = penalties.get(0);
			String rightTask = penalties.get(1);
			int possiblePenalty = Integer.parseInt(penalties.get(2));
			// Check if task has neighbor, if so set penalty value
			if (taskLetter == leftTask) {
				// If at machine 8, check machine 0
				machine = machine==7? 0 : machine+1;
				int tooNearTask = matches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(matches[machine]);
				}
				if (tooNearLetter == rightTask) {
					penalty = possiblePenalty;
				}
			} else if (taskLetter == rightTask) {
				// If at machine 0, check machine 7
				machine = machine==0? 7 : machine-1;
				int tooNearTask = matches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(matches[machine]);
				}
				if (tooNearLetter == leftTask) {
					penalty = possiblePenalty;
				}
			}
		}
		return penalty;
	}
	
	private int getMatchedMach(int[] matches, int task) {
		int machine = -1;
		for (int i=0; i<matches.length; i++) {
			if (matches[i] == task) {
				machine = i;
			}
		}
		return machine;
	}
	
	private void checkTooNearInvalid(Scheduler s, HardConstraints hc,
			ArrayList<String> machines, ArrayList<String> tasks, int mach, int task) {
		ArrayList<ArrayList<String>> newPair = new ArrayList<ArrayList<String>>();
		ArrayList<String> pair = new ArrayList<String>() {{Arrays.asList(machines.get(mach),tasks.get(task));}};
		ArrayList<ArrayList<String>> tooNearConditions = s.getTooNearInvalid();
	
		ArrayList<ArrayList<String>> newForbidden = hc.tooNear(newPair, tooNearConditions);
		hc.addForbidden(s.getForbiddenPairs(), newForbidden);
	}
}
