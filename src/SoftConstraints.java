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
		
		// Find best task for each machine by iterating over possible total penalty scores
		// when machine[i] is must be assigned for that machine
		for (int mach=0; mach<grid.size(); mach++) {
			ArrayList<Integer> row = grid.get(mach);
			// If machine doesn't have match already
			if (matches[mach] == -1) {
				// Base case using first available task
				int firstTask = 0;
				for (int i=0; i<matches.length; i++) {
					if (taskAvailable(matches,i)) { firstTask = i; break; }
				}
				total = iterateRound(s,mach,firstTask,grid,matches) + tooNearPenalty(s.getTasks(),mach,firstTask,matches,s.getTooNearPenalties());
				matches[mach] = firstTask;
				// Iterate over row to see if any other case gives lower penalty
				for (int task=firstTask+1; task<row.size(); task++) {
					// And pairing does not violate too-near invalid pairs
					if (taskAvailable(matches,task) && hc.isValidPair(s.getForbiddenPairs(), machines.get(mach), tasks.get(task))) {
						int taskPenalty = row.get(task) + tooNearPenalty(s.getTasks(),mach,task,matches,s.getTooNearPenalties());
						int roundTotal = iterateRound(s,mach,task,grid,matches) + taskPenalty;
						if (roundTotal < total) {
							total = roundTotal;
							matches[mach] = task;
							// Add new forbidden pairs for new match
							checkTooNearInvalid(s,hc,machines,tasks,mach,task);
						}
					}
				}
			}
			
		}
		
		// Go through matches to get best total score
		total += getTotalPenalties();
		setTotalPenalties(total);
		
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
			int minValue = row.get(0);
			// Find lowest value in row
			for (int t=1; t<row.size(); t++) {
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
	
	int tooNearPenalty(ArrayList<String> tasks, int machineIndex, int taskIndex,
			int[] roundMatches, ArrayList<ArrayList<String>> tooNearList) {
		int penalty = 0;
		String taskLetter = tasks.get(taskIndex);
		// Loop through all too-near triples
		for (ArrayList<String> penalties : tooNearList) {
			// Split values of too-near penalty
			String leftTask = penalties.get(0);
			String rightTask = penalties.get(1);
			int possiblePenalty = Integer.parseInt(penalties.get(2));
			// Check if task has neighbor, if so set penalty value
			int machine = machineIndex;
			if (taskLetter.matches(leftTask)) {
				// If at machine 8, check machine 0
				machine = machine==7? 0 : machine+1;
				// Get actual task Letter for neighbor machine
				int tooNearTask = roundMatches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(roundMatches[machine]);
				}
				// If is right neighbor, add penalty
				if (tooNearLetter.matches(rightTask)) {
					penalty = possiblePenalty;
				}
			} else if (taskLetter.matches(rightTask)) {
				// If at machine 0, check machine 7
				machine = machine==0? 7 : machine-1;
				// Get actual task Letter for neighbor machine
				int tooNearTask = roundMatches[machine];
				String tooNearLetter = "";
				if (tooNearTask != -1) {
					tooNearLetter = tasks.get(roundMatches[machine]);
				}
				// If is left neighbor, add penalty
				if (tooNearLetter.matches(leftTask)) {
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
