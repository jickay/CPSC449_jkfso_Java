import java.util.ArrayList;

public class HardConstraints {
	
	
	// This method checks for machines and tasks that have been paired more than once.
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public ArrayList<ArrayList<String>> forcedPartialAssignment(ArrayList<ArrayList<String>> forcedPairs, String outputfilename) {
        // Takes one input. The forced partial list that has been parsed.
        ArrayList<ArrayList<String>> input = forcedPairs;  // check type
		
        // check length, if more than 8 return error
        if(input.size() > 8) {
            //System.out.println("partial assignment error");
        	Output op = new Output(outputfilename);
        	op.printError(0);
            System.exit(0);
        }

        for(int i = 0; i < input.size(); i++) {
            for(int j = 0; j < input.size(); j++) {
                if(i != j) {
                    if (input.get(i).get(0).equals(input.get(j).get(0)) || input.get(i).get(1).equals(input.get(j).get(1))) {
                        //System.out.println("partial assignment error");
                    	Output op = new Output(outputfilename);
                    	op.printError(0);
                    	System.exit(0);
                    }
                }
            }
        }
        return input;
	}
	
    // This method compares pairs in forcedPairs to pairs in closedPairs
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public ArrayList<ArrayList<String>> forbiddenMachine(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> closedPairs, String outputfilename) {
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
                    //System.out.println("No valid solution possible!");
                	Output op = new Output(outputfilename);
                	op.print();
                	System.exit(0);
                }
            }
        }
        // Now we have verified our forced partial assignment list.
        return closedList;
	}
	
	// Currently only prints console error message
	public ArrayList<ArrayList<String>> tooNear(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> tooNear, String outputfilename) {
		ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
		
		if (forcedPairs.isEmpty()) {
			return returnList;
		}
        
//        ArrayList<String> taskList = new ArrayList<String>();
//        taskList.add("A");
//        taskList.add("B");
//        taskList.add("C");
//        taskList.add("D");
//        taskList.add("E");
//        taskList.add("F");
//        taskList.add("G");
//        taskList.add("H");
//        
//        //Create a list with only unforced tasks
//        for (int x = 0; x < forcedPairs.size(); x++) {
//            for (int y = 0; y < taskList.size(); y++) {
//                if (forcedPairs.get(x).get(1).equals(taskList.get(y))) {
//                    taskList.remove(y);
//                }
//            }
//        }
//        
//        //Create copy of tooNear
//        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
//        for (int p =0; p < tooNear.size(); p++) {
//            temp.add(tooNear.get(p));
//        }
//       
//        
//        //Remove any tooNear tasks relating to forced pairs from temp
//        for (int z = 0; z<taskList.size(); z++) {
//            for (int v = 0; v<temp.size(); v++) {
//                if (!(temp.get(v).get(1).equals(taskList.get(z))) && !(temp.get(v).get(0).equals(taskList.get(z)))) {
//                    temp.remove(v);
//                }
//            }
//        }
//        
//        //If temp size equals total possible combinations of remaining unforced pairs, exit
//        if (taskList.size()*(taskList.size()-1) == temp.size()) {
//            //System.out.println("No valid solution possible!");
//        	Output op = new Output(outputfilename);
//        	op.print();
//        	System.exit(0);
//        }
		
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
								//System.out.println("No valid solution possible!");
								Output op = new Output(outputfilename);
			                	op.print();
								System.exit(0);
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
	
	public void eliminatePairs(ArrayList<ArrayList<Integer>> grid, ArrayList<String> machines,
			ArrayList<String> tasks, ArrayList<ArrayList<String>> pairList) {
		for (ArrayList<String> pair : pairList) {
			int mach = machines.indexOf(pair.get(0));
			int task = tasks.indexOf(pair.get(1));
			grid.get(mach).set(task, -1);
		}
	}
	
	public boolean isValidPair(ArrayList<ArrayList<String>> forbidden, String mach, String task) {
		boolean isValid = true;
		for (ArrayList<String> pairs : forbidden) {
			if (mach.matches(pairs.get(0)) && task.matches(pairs.get(1))) {
				isValid = false;
			}
		}
		return isValid;
	}
}
