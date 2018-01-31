//Version 2

/*
 * Hard constraints:
 * 
 * forced partial assignment: 
 * 		this hard constraint consists of up to 8 pairs (mach,task), with mach in {1,2,3,4,5,6,7,8} and task in {A,B,C,D,E,F,G,H}.
 *      Any assignment, that for any machine mach in one of the pairs does not assign the indicated task task to this machine is
 *      invalid. 
 *      Error handling: if among the pairs are two pairs with the same machine or two pairs with the same task, then the system
 *      				should output "partial assignment error" and stop execution.
 *      
 * forbidden machine: 
 * 		this hard constraint consists of a list of pairs (mach,task), with mach in {1,2,3,4,5,6,7,8} and task in {A,B,C,D,E,F,G,H}.
 *      Any assignment, that assigns to a machine mach a task task that is in this list is invalid.
 *      
 * too-near tasks: 
 * 		this hard constraint consists of a list of pairs (task1,task2), with task1,task2 in {A,B,C,D,E,F,G,H}. Any assignment that
 * 		assigns task1 to a machine i and task2 to machine i+1 (or task1 to machine 8 and task2 to machine 1) is invalid.For all
 * 		these hard constraints, if in their description occurs either a machine that is not in {1,2,3,4,5,6,7,8} or a task that is
 * 		not in {A,B,C,D,E,F,G,H}, then the system should terminate with the message "invalid machine/task".
 */


public class HardConstraints {
	
    // This method checks for machines and tasks that have been paired more than once.
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public boolean forcedPartialAssignment(ArrayList<ArrayList<String>> forcedPairs) {
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
        
		
            // if first for loop variant > 0, enter search loop
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
            
        return true;
	}
	
    // This method compares pairs in forcedPairs to pairs in closedPairs
    // returns true if no repetitions found. Prints error message and terminates execution otherwise.
	public boolean forbiddenMachine(ArrayList<ArrayList<String>> forcedPairs, ArrayList<ArrayList<String>> closedPairs) {
        // this function takes in two inputs, the forced pairs list and closed/forbidden pairs list.        
        ArrayList<ArrayList<String>> forcedList = forcedPairs;
        ArrayList<ArrayList<String>> closedList = closedPairs;
        
        String tempForcedMachine;
        String tempForcedTask;
        String tempClosedMachine;
        String tempClosedTask;
        // For this method we are doing a simple comparison. We need two for loops for this
        //
		// fist for loop
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
        return true;
	}
    
	public void tooNearTasks() {
        // Scott
	}

}
