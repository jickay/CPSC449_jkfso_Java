import java.util.ArrayList;
import java.util.Arrays;

public class SoftConstraints {
	
	private int totalPenalties = 0;
	
	public int getTotalPenalties() { return totalPenalties; }
	public void setTotalPenalties(int total) { totalPenalties = total; }
	
	// General method to run submethods
	public String[] setPenalties(Scheduler s, HardConstraints hc, String[] matches) {
		// Convert string matches to ints
		int[] intMatches = new int[matches.length];
		for (int i=0; i<matches.length; i++) {
			String task = matches[i];
			if (task != null) {
				intMatches[i] = s.getTasks().indexOf(matches[i]);
			} else {
				intMatches[i] = -1;
			}
		}
		// Run penalty algorithm
		intMatches = branchAndBound(s,hc,intMatches);
		
		ArrayList<String> tasks = s.getTasks();
		for (int i=0; i<matches.length; i++) {
			int taskIndex = intMatches[i];
			if (taskIndex > -1) {
				matches[i] = tasks.get(intMatches[i]);
			}
		}
		return matches;
	}
	
	private int[] branchAndBound(Scheduler s, HardConstraints hc, int[] matches) {
		ArrayList<ArrayList<Integer>> grid = s.getMachinePenalties();
		ArrayList<String> machines = s.getMachines();
		ArrayList<String> tasks = s.getTasks();
		
		int total = 0;
		
//		int[] matches = new int[]{-1,-1,-1,-1,-1,-1,-1,-1};
		
		// Find best task for each machine by iterating over possible total penalty scores
		// when machine[i] is must be assigned for that machine
		for (int mach=0; mach<grid.size(); mach++) {
			total = 99999;
			ArrayList<Integer> row = grid.get(mach);
			// Iterate over row
			for (int task=0; task<row.size(); task++) {
				// And pairing does not violate too-near invalid pairs
				if (taskAvailable(matches,task) && hc.isValidPair(s.getForbiddenPairs(), machines.get(mach), tasks.get(task))) {
					int taskPenalty = row.get(task);
					int roundTotal = iterateRound(s,mach,task,grid,matches);
					if (roundTotal < total) {
						total = roundTotal;
						matches[mach] = task;
						// Add new forbidden pairs for new match
						checkTooNearInvalid(s,hc,machines,tasks,mach,task);
					}
				}
			}
		}
		
		// Go through matches to get best total score
		int bestTotal = 0;
		for (int mach=0; mach<matches.length; mach++) {
			bestTotal += grid.get(mach).get(matches[mach]);
		}
		setTotalPenalties(bestTotal);
		
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
	
	private int iterateRound(Scheduler s, int mach, int forcedTask,
			ArrayList<ArrayList<Integer>> grid, int[] matches) {
		// Get lists
		ArrayList<ArrayList<String>> tooNearList = s.getTooNearPenalties();
		ArrayList<String> tasks = s.getTasks();
		// Starting values
		int[] roundMatches = matches.clone();
		roundMatches[mach] = forcedTask;
		int roundTotal = grid.get(mach).get(forcedTask);
		// Add all scores of rows up to current mach
		for (int i=0; i<mach; i++) {
			int prev = grid.get(i).get(roundMatches[i]);
			roundTotal += prev;
		}
		// Check all rows below current mach
		for (int j=mach+1; j<grid.size(); j++) {
			ArrayList<Integer> row = grid.get(j);
			int minValue = 99999;
			// Find lowest value in row
			for (int t=0; t<row.size(); t++) {
				// Only check if task not already matched
				if (taskAvailable(roundMatches,t)) {
					int newValue = row.get(t) + tooNearPenalty(tasks,j,t,roundMatches,tooNearList);
					if (newValue < minValue) {
						minValue = newValue;
						roundMatches[j] = t;
					}
				}
			}
			// Add lowest row value to total
			roundTotal += minValue;
		}
		return roundTotal;
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
				// Get actual task Letter for neighbor machine
				int tooNearTask = matches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(matches[machine]);
				}
				// If is right neighbor, add penalty
				if (tooNearLetter == rightTask) {
					penalty = possiblePenalty;
				}
			} else if (taskLetter == rightTask) {
				// If at machine 0, check machine 7
				machine = machine==0? 7 : machine-1;
				// Get actual task Letter for neighbor machine
				int tooNearTask = matches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(matches[machine]);
				}
				// If is left neighbor, add penalty
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
	
	// Check for any additional too-near invalid pairs and add to forbidden list
	private void checkTooNearInvalid(Scheduler s, HardConstraints hc,
			ArrayList<String> machines, ArrayList<String> tasks, int mach, int task) {
		// Set up data structures
		ArrayList<ArrayList<String>> newPair = new ArrayList<ArrayList<String>>();
		ArrayList<String> pair = new ArrayList<String>() {{Arrays.asList(machines.get(mach),tasks.get(task));}};
		ArrayList<ArrayList<String>> tooNearConditions = s.getTooNearInvalid();
		// Add any new invalid pairs to forbidden list
		ArrayList<ArrayList<String>> newForbidden = hc.tooNear(newPair, tooNearConditions);
		hc.addForbidden(s.getForbiddenPairs(), newForbidden);
	}
}
