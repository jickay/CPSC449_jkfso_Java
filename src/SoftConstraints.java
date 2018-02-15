import java.util.ArrayList;
import java.util.Arrays;

public class SoftConstraints {
	
	private int totalPenalties = 0;
	
	public int getTotalPenalties() { return totalPenalties; }
	public void setTotalPenalties(int total) { totalPenalties = total; }
	
	// General method to run submethods
	public String[] setPenalties(Scheduler s, HardConstraints hc, String[] matches, String outputfilename) {
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
		intMatches = branchAndBound(s,hc,intMatches, outputfilename);
		
		ArrayList<String> tasks = s.getTasks();
		for (int i=0; i<matches.length; i++) {
			int taskIndex = intMatches[i];
			if (taskIndex > -1) {
				matches[i] = tasks.get(intMatches[i]);
			}
		}
		return matches;
	}
	
	private int[] branchAndBound(Scheduler s, HardConstraints hc, int[] matches, String outputfilename) {
		ArrayList<ArrayList<Integer>> grid = s.getMachinePenalties();
		ArrayList<String> machines = s.getMachines();
		ArrayList<String> tasks = s.getTasks();
		
		int bestTotal = getTotalPenalties();
		
		// Find best task for each machine by iterating over possible total penalty scores
		// when machine[i] is must be assigned for that machine
		for (int mach=0; mach<grid.size(); mach++) {
			ArrayList<Integer> row = grid.get(mach);
			// If machine doesn't have match already
			if (matches[mach] == -1) {
				// Base case using first available task
				int firstTask = -1;
				for (int i=0; i<matches.length; i++) {
					if (taskAvailable(matches,i) && hc.isValidPair(s.getForbiddenPairs(),machines.get(mach),tasks.get(i))) {
						firstTask = i; break;
					}
				}
				
				// If machine cannot make any matches, then no solution is possible
				if (firstTask == -1) {
					/* no solution possible! */
					//System.out.println("No solution possible!");
					Output op = new Output(outputfilename);
                	op.print();
					System.exit(0);
				}
				
				// If firstTask get base total to compare with other possible tasks
				int firstTotal = iterateRound(s,hc,mach,firstTask,grid,matches);
				bestTotal = firstTotal;
				
				
				// Iterate over row to see if any other case gives lower penalty
				for (int task=0; task<row.size(); task++) {
					// And pairing does not violate too-near invalid pairs
					if (task != firstTask && taskAvailable(matches,task) && hc.isValidPair(s.getForbiddenPairs(), machines.get(mach), tasks.get(task))) {
						int taskPenalty = row.get(task);
						int roundTotal = iterateRound(s,hc,mach,task,grid,matches);
						if (roundTotal < bestTotal) {
							bestTotal = roundTotal;
							matches[mach] = task;
							// Add new forbidden pairs for new match
							checkTooNearInvalid(s,hc,machines,tasks,mach,task, outputfilename);
						}
					}
				}
				
				// Make match as firstTask since no other match is better
				if (bestTotal == firstTotal) {
					matches[mach] = firstTask;
					// Add new forbidden pairs for new match
					checkTooNearInvalid(s,hc,machines,tasks,mach,firstTask, outputfilename);
				}
			}
		}
		
		// Go through matches to get best total score
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
	
	private int iterateRound(Scheduler s, HardConstraints hc, int mach, int forcedTask,
			ArrayList<ArrayList<Integer>> grid, int[] matches) {
		// Get lists
		ArrayList<ArrayList<String>> tooNearList = s.getTooNearPenalties();
		ArrayList<String> machines = s.getMachines();
		ArrayList<String> tasks = s.getTasks();
		// Starting values
		int[] roundMatches = matches.clone();
		roundMatches[mach] = forcedTask;
		// Starting total for round
		int roundTotal = grid.get(mach).get(forcedTask) + tooNearPenalty(s.getTasks(),mach,forcedTask,roundMatches,tooNearList);
		// Add all scores of rows up to current mach
		for (int i=0; i<mach; i++) {
			int prev = grid.get(i).get(roundMatches[i]);
			roundTotal += prev + tooNearPenalty(s.getTasks(),mach,forcedTask,roundMatches,tooNearList);
		}
		// Check all rows below current mach
		int bestValue;
		for (int j=mach+1; j<grid.size(); j++) {
			ArrayList<Integer> row = grid.get(j);
			// Find first available task to use for base
			int firstTask = -1;
			for (int i=0; i<matches.length; i++) {
				if (i!=forcedTask && taskAvailable(roundMatches,i) && hc.isValidPair(s.getForbiddenPairs(),machines.get(j),tasks.get(i))) {
					firstTask = i; break;
				}
			}
			// If no possible task for this iteration, continue to next one
			int minValue;
			if (firstTask != -1) {
				minValue = row.get(firstTask) + tooNearPenalty(s.getTasks(),j,firstTask,roundMatches,tooNearList);
				bestValue = minValue;
			} else {
				continue;
			}
			// Find lowest value in row
			for (int t=firstTask+1; t<row.size(); t++) {
				if (taskAvailable(roundMatches,t)) {
					int newValue = row.get(t) + tooNearPenalty(tasks,j,t,roundMatches,tooNearList);
					if (newValue < minValue) {
						bestValue = newValue;
						roundMatches[j] = t;
					}
				}
			}
			// If no lower value than firstTask, set roundMatch to firstTask
			if (bestValue == minValue) {
				roundMatches[j] = firstTask;
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
					penalty += possiblePenalty;
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
					penalty += possiblePenalty;
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
			ArrayList<String> machines, ArrayList<String> tasks, int mach, int task, String outputfilename) {
		// Set up data structures
		ArrayList<ArrayList<String>> newPair = new ArrayList<ArrayList<String>>();
		ArrayList<String> pair = new ArrayList<String>();
		pair.add(machines.get(mach));
		pair.add(tasks.get(task));
		newPair.add(pair);
		ArrayList<ArrayList<String>> tooNearConditions = s.getTooNearInvalid();
		// Add any new invalid pairs to forbidden list
		ArrayList<ArrayList<String>> newForbidden = hc.tooNear(newPair, tooNearConditions, outputfilename);
		hc.addForbidden(s.getForbiddenPairs(), newForbidden);
	}
}
