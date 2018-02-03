import java.util.ArrayList;

public class SoftConstraints {
	
	public void setPenalties(Scheduler s, HardConstraints hc) {
		
		
		int[] mins = new int[8];
		int[] matches = new int[8];
		
		matches = branchAndBound(s);
	}
	
	private int[] branchAndBound(Scheduler s) {
		ArrayList<ArrayList<Integer>> grid = s.getMachinePenalties();
		
		int[] matches = new int[8];
		int[] freeTasks = new int[] {-1,-1,-1,-1,-1,-1,-1,-1};
		
		// Find best task for each machine by iterating over possible total penalty scores
		// when machine[i] is a forced selection
		for (int mach=0; mach<grid.size(); mach++) {
			ArrayList<Integer> row = grid.get(mach);
			// Start with first value in row
			int bestTotal = iterateRound(s,mach,0,row.get(0),grid,matches);
			matches[mach] = 0;
			freeTasks[0] = 1;
			// Iterate over row
			for (int task=1; task<row.size(); task++) {
				// Check only if spot not taken by previous row
				if (freeTasks[task] == -1) {
					int taskValue = row.get(task);
					int roundTotal = iterateRound(s,mach,task,taskValue,grid,matches);
					if (roundTotal < bestTotal) {
						bestTotal = roundTotal;
						matches[mach] = task;
					}
				}
			}
		}
		
		return matches;
	}
	
	private int iterateRound(Scheduler s, 
			int mach, int task, int taskValue,
			ArrayList<ArrayList<Integer>> grid, int[] matches) {
		ArrayList<ArrayList<String>> tooNearList = s.getTooNearPenalties();
		ArrayList<String> tasks = s.getTasks();
		// Keep track of free and assigned tasks
		int[] freeTasks = new int[] {-1,-1,-1,-1,-1,-1,-1,-1};
		// Starting values
		freeTasks[task] = mach;
		int totalScore = taskValue;
		// Check all rows below current i
		for (int j=mach+1; j<grid.size(); j++) {
			ArrayList<Integer> row = grid.get(j);
			int minValue = row.get(0);
			// Find lowest value in row
			for (int t=1; t<row.size(); t++) {
				// Check only if free
				if (freeTasks[t] == -1) {
					int newValue = row.get(t) + tooNearPenalty(tasks,t,matches,tooNearList);
					if (newValue < minValue) {
						minValue = newValue;
						freeTasks[j] = 1;
					}
				}
			}
			// Add lowest row value to total
			totalScore += minValue;
		}
		return totalScore;
	}
	
	private int tooNearPenalty(ArrayList<String> tasks, int taskIndex,
			int[] matches, ArrayList<ArrayList<String>> tooNearList) {
		int penalty = 0;
		for (ArrayList<String> penalties : tooNearList) {
			// Get machine matches to this task
			
			int machine;
			for (int i=0; i<matches.length; i++) {
				if (matches[i] == taskIndex) {
					machine = i;
				}
			}
			// Split values of too-near penalty
			String leftTask = penalties.get(0);
			String rightTask = penalties.get(1);
			int possiblePenalty = Integer.parseInt(penalties.get(2));
			// Check if task has neighbor, if so set penalty value
			if (taskIndex == leftTask) {
				// If at machine 8, check machine 0
				machine = machine==7? 0 : machine+1;
				if (matches.get(machine) == rightTask) {
					penalty = possiblePenalty;
				}
			} else if (task == rightTask) {
				// If at machine 0, check machine 7
				machine = machine==0? 7 : machine-1;
				if (matches.get(machine) == leftTask) {
					penalty = possiblePenalty;
				}
			}
		}
		return penalty;
	}
	
}
