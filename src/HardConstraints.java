import java.util.ArrayList;

public class HardConstraints {
	
	
	// This method checks for machines and tasks that have been paired more than once.
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public ArrayList<ArrayList<String>> forcedPartialAssignment(ArrayList<ArrayList<String>> forcedPairs) {
        // Takes one input. The forced partial list that has been parsed.
        ArrayList<ArrayList<String>> input = forcedPairs;  // check type
		
        // check length, if more than 8 return error
        if(input.size() > 8) {
            System.out.println("partial assignment error");
            System.exit(0);
        }
        
        for(int i = 0; i < input.size(); i++) {
            
            String [] machineArray = new String[input.size()]; // length of array = number of elements in linked list
            String [] taskArray = new String[input.size()]; // length of array = number of elements in linked list

            machineArray[i] = input.get(i).get(0); // machine from the i'th pair is stored in an array
            taskArray[i] = input.get(i).get(1); // task from the i'th pair is stored in an array
        
		
            // Check for doubles if more than one forced pair
            if(i > 0) {
                
                //	here we check if machine[i] is already in our array
                for(int check = i - 1; check >= 0; check--) {
                    // if machine[i] is in the array return error
                    if(machineArray[i] == machineArray[check]) {
                        System.out.println("partial assignment error");
                        System.exit(0);
                    }
                    
                    // if task[i] is in the array return error
                    if(taskArray[i] == taskArray[check]) {
                        System.out.println("partial assignment error");
                        System.exit(0);
                    }
                }
            }
        }
            
        return input;
	}
	
    // This method compares pairs in forcedPairs to pairs in closedPairs
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public ArrayList<ArrayList<String>> forbiddenMachine(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> closedPairs) {
        // this function takes in two inputs, the forced pairs list and closed/forbidden pairs list.        
        ArrayList<ArrayList<String>> forcedList = forcedPairs;
        ArrayList<ArrayList<String>> closedList = closedPairs;
        
        String tempForcedMachine;
        String tempForcedTask;
        String tempClosedMachine;
        String tempClosedTask;
        
        // Check if forbidden list conflicts with forced list
        for(int i = 0; i < forcedList.size(); i++) {
            tempForcedMachine = forcedList.get(i).get(0); // machine in i'th pair from forcedPairs
            tempForcedTask = forcedList.get(i).get(1); // task in i'th pair from forcedPairs
            
            for(int j = 0; j < closedList.size(); j++) {
                tempClosedMachine = closedList.get(j).get(0); // machine in j'th pair from closedPairs
                tempClosedTask = closedList.get(j).get(1); // task in j'th pair from closedPairs
                
                // if i'th forced pair is == to j'th closed pair then return error
                if(tempForcedMachine == tempClosedMachine && tempForcedTask == tempClosedTask) {
                    System.out.println("list invalid");
                    System.exit(0);
                }
            }
        }
        // Now we have verified our forced partial assignment list.
        return closedList;
	}
	
	// Currently only prints console error message
	public ArrayList<ArrayList<String>> tooNear(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> tooNear) {
		ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
		
		if (forcedPairs.isEmpty()) {
			return returnList;
		}
		
		for (int i = 0; i < forcedPairs.size(); i++) {
			//Get pairing in list of forced pairs
			ArrayList<String> pairing = forcedPairs.get(i);
			int machine = Integer.parseInt(pairing.get(0));
			String task = pairing.get(1);
			//For each pairing in tooNear list, check if forced pair task is the same
			for (int j = 0; j < tooNear.size(); j++) {
				if (task.equals(tooNear.get(j).get(0)) || task.equals(tooNear.get(j).get(1))) {
					//If tasks are equal search forced pair list for new pairing with machine i+1
					for (int k = 0; k < forcedPairs.size(); k++) {
						ArrayList<String> pairingPlus = forcedPairs.get(k);
						int machinePlus = Integer.parseInt(pairingPlus.get(0));
						String taskPlus = pairingPlus.get(1);
						// If machine of the two pairings are i and i+1
						if (machinePlus == machine+1 || (machine == 8 && machinePlus == 1)) {
							// If task of new machine i+1 is in the too near pair print error
							if (taskPlus.equals(tooNear.get(j).get(0)) || taskPlus.equals(tooNear.get(j).get(1))) {
								System.out.println("Invalid forced pairs + near pairs");
							}
						}
					}
					ArrayList<String> ip = new ArrayList<String>();
					if (task.equals(tooNear.get(j).get(0))) {
						if ((machine + 1) == 9) {
							ip.add("1");
						}
						else {
							ip.add(Integer.toString(machine + 1));
						}
						ip.add(tooNear.get(j).get(1));
						returnList.add(ip);
					}
					else if (task.equals(tooNear.get(j).get(1))) {
						if ((machine - 1) == 0) {
							ip.add("8");
						}
						else {
							ip.add(Integer.toString(machine - 1));
						}
						ip.add(tooNear.get(j).get(0));
						returnList.add(ip);
					}
				}
			}
		}
		return returnList;
	}
	
	public void addForbidden(ArrayList<ArrayList<String>> forbidden, ArrayList<ArrayList<String>> list) {
		for (ArrayList<String> pair : list) {
			forbidden.add(pair);
		}
	}
	
	public ArrayList<ArrayList<String>> eliminatePairs(ArrayList<ArrayList<String>> grid, ArrayList<ArrayList<String>> pairList) {
		for (int i = 0; i < pairList.size(); i++) {
			for (int j = 0; j < grid.size(); j++) {
				if (grid.get(j).get(0).equals(pairList.get(i).get(0)) && grid.get(j).get(1).equals(pairList.get(i).get(1))) {
					ArrayList<String> neg1 = new ArrayList<String>();
					neg1.add("-1");
					neg1.add("-1");
					grid.set(j, neg1);
				}
			}
		}
		return grid;
	}
	
	public boolean isValidPair(ArrayList<ArrayList<String>> forbidden, String mach, String task) {
		boolean isValid = true;
		for (ArrayList<String> pairs : forbidden) {
			if (mach == pairs.get(0) && task == pairs.get(1)) {
				isValid = false;
			}
		}
		return isValid;
	}
}